package backend.academy.scrapper.config;

import backend.academy.scrapper.ScrapperConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {
    private final ScrapperConfig scrapperConfig;
    private final KafkaProperties kafkaProperties;
    private final KafkaAdmin kafkaAdmin;

    @PostConstruct
    public void createTopics() {
        kafkaAdmin.createOrModifyTopics(
                new NewTopic(
                        scrapperConfig.kafka().updatesTopic(),
                        scrapperConfig.kafka().partitions(),
                        (short) 1),
                new NewTopic(
                        scrapperConfig.kafka().dlqTopic(),
                        scrapperConfig.kafka().partitions(),
                        (short) 1));
    }

    @Bean
    public KafkaTemplate<Long, String> jsonKafkaTemplate() {
        var props = kafkaProperties.buildProducerProperties(null);

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        var factory = new DefaultKafkaProducerFactory<Long, String>(props);
        return new KafkaTemplate<>(factory);
    }
}
