package backend.academy.scrapper.clients;

import backend.academy.dto.LinkUpdate;
import backend.academy.logger.LoggerHelper;
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

    public Mono<String> postUpdates(LinkUpdate linkUpdate) {
        try {
            String json = new ObjectMapper().writeValueAsString(linkUpdate);
            return webClient
                    .post()
                    .uri("/updates")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(json)
                    .retrieve()
                    .bodyToMono(String.class);
        } catch (JsonProcessingException e) {
            LoggerHelper.error("Error while converting update to json", e);
        }
        return Mono.empty();
    }
}
