package backend.academy.scrapper.clients;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GitHubClient {
    private final WebClient webClient;

    @Autowired
    public GitHubClient(WebClient gitHubWebClient) {
        this.webClient = gitHubWebClient;
    }

    public Mono<GitHubNotifications> getNewNotifications(String url, String lastModified) {
        String[] address = parseGitHubUrl(url);
        if (address == null || address.length != 2) {
            return Mono.just(new GitHubNotifications("Incorrect url:" + url, lastModified));
        }
        WebClient.RequestHeadersSpec<?> request =
                webClient.get().uri("/repos/{owner}/{repo}/events", address[0], address[1]);

        if (lastModified != null && !lastModified.isEmpty()) {
            request.header(HttpHeaders.IF_MODIFIED_SINCE, lastModified);
        }

        return request.retrieve()
                .onStatus(HttpStatus.NOT_MODIFIED::equals, response -> Mono.empty())
                .bodyToMono(JsonNode.class)
                .doOnSuccess(response -> log.info("Raw GitHub response: {}", response))
                .map(response -> {
                    if (!response.isEmpty()) {
                        JsonNode latestEvent = response.get(0);
                        String type = latestEvent.get("type").asText();
                        String createdAt = latestEvent.get("created_at").asText();
                        return new GitHubNotifications("New event: " + type, createdAt);
                    }
                    return new GitHubNotifications("No new events", lastModified);
                })
                .switchIfEmpty(Mono.just(new GitHubNotifications("No updates", lastModified)))
                .onErrorResume(e -> {
                    log.error("Error retrieving data", e);
                    return Mono.just(new GitHubNotifications("Error retrieving data", lastModified));
                });
    }

    private String[] parseGitHubUrl(String url) {
        if (url.startsWith("github.com/")) {
            return url.substring("github.com/".length()).split("/");
        }
        return null;
    }
}
