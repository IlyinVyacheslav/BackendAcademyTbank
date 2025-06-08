package backend.academy.bot.controller;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import backend.academy.bot.config.CommonKafkaConfig;
import backend.academy.bot.config.KafkaConsumerConfig;
import backend.academy.bot.service.BotService;
import backend.academy.dto.LinkUpdate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = {ChatUpdatesConsumer.class, KafkaConsumerConfig.class, CommonKafkaConfig.class})
@ImportAutoConfiguration(KafkaAutoConfiguration.class)
@EmbeddedKafka(topics = "${app.kafka.updates-topic}")
public class ChatUpdatesConsumerTest {

    @Value("${app.kafka.updates-topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<Long, LinkUpdate> kafkaTemplate;

    @MockitoBean
    private BotService botService;

    @Autowired
    private ChatUpdatesConsumer chatUpdatesConsumer;

    @Test
    void shouldListenFromUpdatesTopic() {
        LinkUpdate update = new LinkUpdate(1L, "url", "description", List.of(1L));
        kafkaTemplate.send(topic, 1L, update);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(botService, times(1)).sendMessage(any(), any());
        });
    }

    @Test
    void shouldSendNotificationsToMultipleChats() {
        LinkUpdate update = new LinkUpdate(1L, "url", "description", List.of(1L, 2L));
        kafkaTemplate.send(topic, 1L, update);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(botService, times(1)).sendMessage(eq(String.valueOf(1L)), any());
            verify(botService, times(1)).sendMessage(eq(String.valueOf(2L)), any());
        });
    }

    @Test
    void shouldCorrectlyDeserializeValidJsonMessage() {
        LinkUpdate expectedUpdate =
                new LinkUpdate(1L, "https://example.com", "Example description", List.of(1L, 2L, 3L));

        kafkaTemplate.send(topic, 1L, expectedUpdate);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(botService, times(3))
                    .sendMessage(
                            any(),
                            eq(String.format(
                                    "\"%s\" url was updated: %s", expectedUpdate.url(), expectedUpdate.description())));

            expectedUpdate.tgChatIds().forEach(chatId -> {
                verify(botService, times(1)).sendMessage(eq(String.valueOf(chatId)), any());
            });
        });
    }
}
