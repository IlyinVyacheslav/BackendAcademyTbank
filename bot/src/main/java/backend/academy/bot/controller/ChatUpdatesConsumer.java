package backend.academy.bot.controller;

import backend.academy.bot.service.BotService;
import backend.academy.dto.LinkUpdate;
import backend.academy.logger.LoggerHelper;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class ChatUpdatesConsumer extends BaseChatUpdatesController {

    @Autowired
    public ChatUpdatesConsumer(BotService botService) {
        super(botService);
    }

    @SneakyThrows
    @KafkaListener(containerFactory = "consumerFactory", topics = "${app.kafka.updates-topic}")
    public void consume(ConsumerRecord<Long, LinkUpdate> record, Acknowledgment acknowledgement) {
        LoggerHelper.info(String.format(
                "Получено следующее сообщение из топика %s:%n key: %s, value: %s",
                record.topic(), record.key(), record.value()));
        acknowledgement.acknowledge();
        processLinkUpdate(record.value());
    }
}
