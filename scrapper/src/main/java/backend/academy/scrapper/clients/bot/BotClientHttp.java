package backend.academy.scrapper.clients.bot;

import backend.academy.dto.LinkUpdate;
import backend.academy.logger.LoggerHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BotClientHttp implements BotClient {
    private final WebClient webClient;

    @Override
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
