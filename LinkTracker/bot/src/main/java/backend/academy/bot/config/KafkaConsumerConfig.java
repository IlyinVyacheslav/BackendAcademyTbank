package backend.academy.bot.config;

import backend.academy.dto.LinkUpdate;
import java.util.Map;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.RoundRobinAssignor;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonLoggingErrorHandler;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class KafkaConsumerConfig {
    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Long, LinkUpdate>> consumerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<Long, LinkUpdate>();
        factory.setConsumerFactory(buildConsumerFactory(
                LinkUpdateDeserializer.class, props -> props.put(ConsumerConfig.GROUP_ID_CONFIG, "default-consumer")));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setCommonErrorHandler(new CommonLoggingErrorHandler());
        factory.setAutoStartup(true);
        factory.setConcurrency(1);
        factory.getContainerProperties().setGroupId("default-consumer");
        return factory;
    }

    private <T> ConsumerFactory<Long, T> buildConsumerFactory(
            Class<? extends Deserializer<T>> valueDeserializerClass, Consumer<Map<String, Object>> propsModifier) {
        var props = kafkaProperties.buildConsumerProperties(null);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializerClass);

        props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, RoundRobinAssignor.class.getName());

        propsModifier.accept(props);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    public static class LinkUpdateDeserializer extends JsonDeserializer<LinkUpdate> {}
}
