package backend.academy.scrapper.config;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
@RequiredArgsConstructor
public class CommonKafkaConfig {
    private final KafkaProperties kafkaProperties;

    @Bean
    Admin localKafkaClusterAdminClient() {
        return AdminClient.create(
                Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers()));
    }

    @Bean
    KafkaAdmin localKafkaClusterAdmin() {
        return new KafkaAdmin(
                Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers()));
    }
}
