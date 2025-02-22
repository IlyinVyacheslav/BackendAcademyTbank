package backend.academy.scrapper.clients;

import backend.academy.dto.Update;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class BotClient {
    private final WebClient webClient;

    @Autowired
    public BotClient(WebClient botWebClient) {
        this.webClient = botWebClient;
    }

    public Mono<String> postUpdates(Update update) {
        try {
            String json = new ObjectMapper().writeValueAsString(update);
            log.info("JSON: {}", json);
            return webClient
                    .post()
                    .uri("/updates")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(json)
                    .retrieve()
                    .bodyToMono(String.class);
        } catch (JsonProcessingException e) {
            log.error("Error while converting update to json: {}", e.getMessage());
        }
        return Mono.empty();
    }
}
