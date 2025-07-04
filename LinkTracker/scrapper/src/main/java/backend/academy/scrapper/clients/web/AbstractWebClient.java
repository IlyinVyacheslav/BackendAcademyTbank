package backend.academy.scrapper.clients.web;

import backend.academy.logger.LoggerHelper;
import backend.academy.scrapper.clients.Notifications;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import java.sql.Timestamp;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
public abstract class AbstractWebClient implements WebSiteClient {
    protected final WebClient webClient;

    protected AbstractWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    protected Mono<Notifications> fetchNotifications(String uri, Timestamp lastModified) {
        WebClient.RequestHeadersSpec<?> request = webClient.get().uri(uri);

        if ("github.com".equals(URI.create(uri).getHost()) && lastModified != null) {
            request.header(HttpHeaders.IF_MODIFIED_SINCE, lastModified.toString());
        }

        return request.retrieve()
                .onStatus(HttpStatus.NOT_MODIFIED::equals, response -> Mono.empty())
                .bodyToMono(JsonNode.class)
                .doOnSuccess(response -> LoggerHelper.info(
                        "Raw response from " + getClass().getSimpleName(), Map.of("response", response)))
                .map(response -> processResponse(response, lastModified))
                .switchIfEmpty(Mono.just(new Notifications("No updates", lastModified)))
                .onErrorResume(e -> {
                    LoggerHelper.error(
                            "Error retrieving data from " + getClass().getSimpleName(), e);
                    return Mono.just(new Notifications("Error retrieving data", lastModified));
                });
    }

    protected abstract Notifications processResponse(JsonNode response, Timestamp lastModified);
}
