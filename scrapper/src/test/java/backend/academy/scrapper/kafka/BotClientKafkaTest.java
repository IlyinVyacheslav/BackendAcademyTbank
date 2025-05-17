package backend.academy.scrapper.kafka;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.dto.LinkUpdate;
import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.clients.bot.BotClientKafka;
import backend.academy.scrapper.config.CommonKafkaConfig;
import backend.academy.scrapper.config.KafkaProducerConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import reactor.test.StepVerifier;

@SpringBootTest(
        classes = {
            BotClientKafkaTest.TestConfig.class,
            BotClientKafka.class,
            CommonKafkaConfig.class,
            KafkaProducerConfig.class,
        })
@ImportAutoConfiguration(KafkaAutoConfiguration.class)
@EmbeddedKafka(
        topics = {"${app.kafka.updates-topic}", "${app.kafka.dlq-topic}"},
        partitions = 1)
@DirtiesContext
public class BotClientKafkaTest {
    @Autowired
    @MockitoSpyBean
    private KafkaTemplate<Long, String> kafkaTemplate;

    @MockitoSpyBean
    private ObjectMapper objectMapper;

    @Autowired
    private BotClientKafka botClientKafka;

    @Value("${app.kafka.updates-topic}")
    private String updatesTopic;

    @Value("${app.kafka.dlq-topic}")
    private String dlqTopic;

    @Test
    public void shouldSendValidUpdateToKafka() {
        LinkUpdate linkUpdate = new LinkUpdate(1L, "https://example.com", "Description", List.of(1L, 2L));

        StepVerifier.create(botClientKafka.postUpdates(linkUpdate))
                .expectNext("Sent to kafka")
                .verifyComplete();

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(kafkaTemplate)
                    .send(eq(updatesTopic), eq(linkUpdate.id()), eq(new ObjectMapper().writeValueAsString(linkUpdate)));
        });
    }

    @Test
    public void shouldSendInvalidUpdateToDlqWhenSerializationFails() throws JsonProcessingException {
        LinkUpdate linkUpdate = new LinkUpdate(1L, "https://example.com", "Description", List.of(1L, 2L));
        JsonProcessingException mockException = new JsonProcessingException("Serialization error") {};
        when(objectMapper.writeValueAsString(any(LinkUpdate.class))).thenThrow(mockException);

        StepVerifier.create(botClientKafka.postUpdates(linkUpdate))
                .expectErrorMatches(throwable -> throwable == mockException)
                .verify();

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(kafkaTemplate).send(eq(dlqTopic), eq(linkUpdate.id()), eq(mockException.getMessage()));
        });
        verify(kafkaTemplate, never()).send(eq(updatesTopic), any(), any());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ScrapperConfig scrapperConfig(
                @Value("${app.kafka.updates-topic}") String updatesTopic,
                @Value("${app.kafka.dlq-topic}") String dlqTopic) {
            return new ScrapperConfig(
                    "test-github-token",
                    new ScrapperConfig.StackOverflowCredentials("test-key", "test-access-token"),
                    10,
                    "SQL",
                    "Kafka",
                    new ScrapperConfig.Kafka(updatesTopic, dlqTopic, 1));
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}
