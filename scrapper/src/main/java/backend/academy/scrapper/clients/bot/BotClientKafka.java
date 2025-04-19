package backend.academy.scrapper.clients.bot;

import backend.academy.dto.LinkUpdate;
import backend.academy.logger.LoggerHelper;
import backend.academy.scrapper.ScrapperConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BotClientKafka implements BotClient {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final ScrapperConfig config;

    @Override
    public Mono<String> postUpdates(LinkUpdate linkUpdate) {
        try {
            String json = objectMapper.writeValueAsString(linkUpdate);
            kafkaTemplate.send(config.kafka().updatesTopic(), json);
            return Mono.just("Sent to kafka");
        } catch (JsonProcessingException e) {
            LoggerHelper.error("Kafka send error: unable to serialize update", e);
            kafkaTemplate.send(config.kafka().dlqTopic(), e.getMessage());
            return Mono.error(e);
        }
    }
}
