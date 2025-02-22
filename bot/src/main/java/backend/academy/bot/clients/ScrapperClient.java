package backend.academy.bot.clients;

import backend.academy.dto.AddLinkRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ScrapperClient {
    private final WebClient webClient;

    @Autowired
    public ScrapperClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<String> registerChat(Long chatId) {
        return webClient.post().uri("/tg-chat/{id}", chatId).retrieve().bodyToMono(String.class);
    }

    public Mono<String> deleteChat(Long chatId) {
        return webClient.delete().uri("/tg-chat/{id}", chatId).retrieve().bodyToMono(String.class);
    }

    public Mono<List<String>> getAllLinks(Long chatId) {
        return webClient
                .get()
                .uri("/links")
                .header("Tg-Chat-Id", chatId.toString())
                .retrieve()
                .bodyToFlux(String.class)
                .collectList();
    }

    public Mono<String> addLink(Long chatId, AddLinkRequest linkRequest) {
        try {
            String json = new ObjectMapper().writeValueAsString(linkRequest);
            log.info("JSON: {}", json);
            return webClient
                    .post()
                    .uri("/links")
                    .header("Tg-Chat-Id", chatId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(json)
                    .retrieve()
                    .bodyToMono(String.class);
        } catch (JsonProcessingException e) {
            log.error("Error while converting to json", e);
        }
        return Mono.empty();
    }

    public Mono<Void> removeLink(Long chatId, String link) {
        return webClient
                .method(HttpMethod.DELETE)
                .uri("/links")
                .header("Tg-Chat-Id", chatId.toString())
                .bodyValue(link)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
