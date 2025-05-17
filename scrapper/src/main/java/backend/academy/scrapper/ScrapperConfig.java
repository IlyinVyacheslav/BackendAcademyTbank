package backend.academy.scrapper;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public record ScrapperConfig(
        @NotEmpty String githubToken,
        StackOverflowCredentials stackOverflow,
        int pageSize,
        String accessType,
        String messageTransport,
        Kafka kafka) {
    public record StackOverflowCredentials(@NotEmpty String key, @NotEmpty String accessToken) {}

    public record Kafka(String updatesTopic, String dlqTopic, int partitions) {}
}
