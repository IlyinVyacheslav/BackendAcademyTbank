package backend.academy.bot.controller;

import backend.academy.bot.config.CommonKafkaConfig;
import backend.academy.bot.config.KafkaConsumerConfig;
import backend.academy.bot.service.BotService;
import backend.academy.dto.LinkUpdate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Testcontainers
@SpringBootTest(classes = {
    ChatUpdatesConsumer.class,
    KafkaConsumerConfig.class,
    CommonKafkaConfig.class})
@ImportAutoConfiguration(KafkaAutoConfiguration.class)
public class ChatUpdatesConsumerTest {

    @Container
    @ServiceConnection
    private static final KafkaContainer kafkaContainer =
        new KafkaContainer("apache/kafka:latest");

    @Value("${app.kafka.updates-topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<Long, LinkUpdate> kafkaTemplate;

    @MockitoBean
    private BotService botService;

    @Autowired
    private ChatUpdatesConsumer chatUpdatesConsumer;


    @Test
    void shouldListenFromUpdatesTopic() throws ExecutionException, InterruptedException {
        LinkUpdate update = new LinkUpdate(1L, "url", "description", List.of(1L));
        kafkaTemplate.send(topic, 1L, update).get();

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(botService, times(1)).sendMessage(any(), any());
        });

    }
}
