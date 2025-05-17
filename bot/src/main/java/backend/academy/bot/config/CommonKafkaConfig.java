package backend.academy.bot.config;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CommonKafkaConfig {
    private final KafkaProperties kafkaProperties;

    @Bean
    Admin localKafkaClusterAdminClient() {
        Map<String, Object> conf = kafkaProperties.buildProducerProperties();
        return AdminClient.create(conf);
    }
}
