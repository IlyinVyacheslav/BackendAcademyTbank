package backend.academy.scrapper.clients;

import backend.academy.logger.LoggerHelper;
import com.fasterxml.jackson.databind.JsonNode;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GitHubClient extends AbstractWebClient {

    @Autowired
    public GitHubClient(WebClient gitHubWebClient) {
        super(gitHubWebClient);
    }

    @Override
    public Mono<Notifications> getNewNotifications(String url, Timestamp lastModified) {
        String[] address = parseGitHubUrl(url);
        if (address == null || address.length != 2) {
            return Mono.just(new Notifications("Incorrect url:" + url, lastModified));
        }
        String uri = "/repos/" + address[0] + "/" + address[1] + "/events";
        return fetchNotifications(uri, lastModified);
    }

    @Override
    protected Notifications processResponse(JsonNode response, Timestamp lastModified) {
        if (!response.isEmpty()) {
            JsonNode latestEvent = response.get(0);
            String type = latestEvent.get("type").asText();
            String createdAtString = latestEvent.get("created_at").asText();
            LoggerHelper.info("Real time: " + createdAtString);
            Timestamp createdAt =
                    Timestamp.from(OffsetDateTime.parse(createdAtString).toInstant());
            return new Notifications("New event: " + type, createdAt);
        }
        return new Notifications("No new events", lastModified);
    }

    private String[] parseGitHubUrl(String url) {
        if (url.startsWith("github.com/")) {
            return url.substring("github.com/".length()).split("/");
        }
        return null;
    }
}
