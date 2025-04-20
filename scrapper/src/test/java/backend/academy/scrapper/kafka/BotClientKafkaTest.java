package backend.academy.scrapper.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.dto.LinkUpdate;
import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.clients.bot.BotClientKafka;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
public class BotClientKafkaTest {

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @DynamicPropertySource
    static void setKafkaProps(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Autowired
    private BotClientKafka botClientKafka;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ScrapperConfig scrapperConfig;

    private final LinkUpdate linkUpdate = new LinkUpdate(1L, "http://example.com", "testDescription", List.of(1L));

    private Consumer<String, String> createConsumer(String topic) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        Consumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(List.of(topic));
        return consumer;
    }

    @Test
    public void testPostUpdates_ValidMessage_SendsToKafka() throws Exception {
        String result = botClientKafka.postUpdates(linkUpdate).block();
        assertThat(result).isEqualTo("Sent to kafka");

        try (Consumer<String, String> consumer =
                createConsumer(scrapperConfig.kafka().updatesTopic())) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
            assertThat(records.count()).isGreaterThan(0);

            var record = records.iterator().next();
            assertThat(record.key()).isEqualTo(linkUpdate.id().toString());
            assertThat(record.value()).isEqualTo(objectMapper.writeValueAsString(linkUpdate));
        }
    }

    @Test
    public void testPostUpdates_InvalidMessage_SendsToDLQ() {
        LinkUpdate invalidUpdate = new LinkUpdate(null, "http://example.com", "testDescription", List.of(1L));

        botClientKafka.postUpdates(invalidUpdate).block();

        try (Consumer<String, String> consumer =
                createConsumer(scrapperConfig.kafka().dlqTopic())) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
            assertThat(records.count()).isGreaterThan(0);

            var dlqRecord = records.iterator().next();
            assertThat(dlqRecord.key()).isEqualTo("null");
            assertThat(dlqRecord.value()).contains("Kafka send error"); // точную формулировку можно уточнить
        }
    }
}
