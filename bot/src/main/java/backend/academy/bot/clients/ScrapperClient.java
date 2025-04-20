package backend.academy.bot.clients;

import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.ApiErrorResponse;
import backend.academy.logger.LoggerHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class ScrapperClient {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public ScrapperClient(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    private String parseErrorResponse(String responseBody) {
        try {
            ApiErrorResponse errorResponse = objectMapper.readValue(responseBody, ApiErrorResponse.class);
            return String.format(
                    "❌ %s: %s(%s)",
                    errorResponse.exceptionName(), errorResponse.exceptionMessage(), errorResponse.description());
        } catch (JsonProcessingException e) {
            return "❌ Неизвестная ошибка сервера.";
        }
    }

    private <T> Mono<T> sendRequest(HttpMethod method, String uri, Long chatId, Object body, Class<T> responseType) {
        WebClient.RequestBodySpec requestSpec = webClient.method(method).uri(uri);

        if (chatId != null) {
            requestSpec.header("Tg-Chat-Id", chatId.toString());
        }

        if (body != null) {
            requestSpec.contentType(MediaType.APPLICATION_JSON).bodyValue(body);
        }

        return requestSpec
                .retrieve()
                .bodyToMono(responseType)
                .onErrorResume(WebClientResponseException.class, ex -> Mono.just(
                                parseErrorResponse(ex.getResponseBodyAsString()))
                        .cast(responseType));
    }

    public Mono<String> registerChat(Long chatId) {
        return sendRequest(HttpMethod.POST, "/tg-chat/" + chatId, null, null, String.class);
    }

    public Mono<String> deleteChat(Long chatId) {
        return sendRequest(HttpMethod.DELETE, "/tg-chat/" + chatId, null, null, String.class);
    }

    public Mono<List<String>> getAllLinks(Long chatId) {
        return webClient
                .get()
                .uri("/links")
                .header("Tg-Chat-Id", chatId.toString())
                .retrieve()
                .bodyToFlux(String.class)
                .onErrorResume(
                        WebClientResponseException.class,
                        ex -> Mono.just(parseErrorResponse(ex.getResponseBodyAsString())))
                .collectList();
    }

    public Mono<List<String>> getLinksByTag(Long chatId, String tag) {
        return webClient
                .method(HttpMethod.GET)
                .uri(uriBuilder ->
                        uriBuilder.path("/links/tagged").queryParam("tag", tag).build())
                .header("Tg-Chat-Id", chatId.toString())
                .retrieve()
                .bodyToFlux(String.class)
                .onErrorResume(
                        WebClientResponseException.class,
                        ex -> Mono.just(parseErrorResponse(ex.getResponseBodyAsString())))
                .collectList();
    }

    public Mono<List<String>> getLinksByTagAndTime(Long chatId, String tag, Timestamp time) {
        return webClient
                .method(HttpMethod.GET)
                .uri(uriBuilder ->
                        uriBuilder.path("/links/updates").queryParam("tag", tag).build())
                .header("Tg-Chat-Id", chatId.toString())
                .bodyValue(time.getTime())
                .retrieve()
                .bodyToFlux(String.class)
                .onErrorResume(
                        WebClientResponseException.class,
                        ex -> Mono.just(parseErrorResponse(ex.getResponseBodyAsString())))
                .collectList();
    }

    public Mono<String> addLink(Long chatId, AddLinkRequest linkRequest) {
        try {
            String json = new ObjectMapper().writeValueAsString(linkRequest);
            return sendRequest(HttpMethod.POST, "/links", chatId, json, String.class);
        } catch (JsonProcessingException e) {
            LoggerHelper.error("Error while converting to json", e);
        }
        return Mono.empty();
    }

    public Mono<String> removeLink(Long chatId, String link) {
        return sendRequest(HttpMethod.DELETE, "/links", chatId, link, String.class);
    }
}
