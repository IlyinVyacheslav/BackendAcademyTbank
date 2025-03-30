package backend.academy.scrapper.clients.web;

import backend.academy.logger.LoggerHelper;
import backend.academy.scrapper.clients.Notifications;
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
        LoggerHelper.info("GitHub response" + response);
        if (!response.isEmpty()) {
            JsonNode latestEvent = response.get(0);
            String type = latestEvent.get("type").asText();
            String createdAtString = latestEvent.get("created_at").asText();
            Timestamp createdAt =
                    Timestamp.from(OffsetDateTime.parse(createdAtString).toInstant());
            LoggerHelper.info("Latest event: " + latestEvent);

            String message;
            if ("PushEvent".equals(type)) {
                JsonNode commit = latestEvent.path("payload").path("commits").get(0);
                String commitMessage = commit.path("message").asText("No commit message");
                String user = commit.path("author").path("name").asText("Unknown user");
                message = "New push by " + user + " | Commit: " + commitMessage;
            } else if ("IssuesEvent".equals(type) || "PullRequestEvent".equals(type)) {
                JsonNode issueOrPr = latestEvent.path("payload").path("issue");
                if (issueOrPr.isMissingNode()) {
                    issueOrPr = latestEvent.path("payload").path("pull_request");
                }
                String title = issueOrPr.path("title").asText("Unknown title");
                String user = latestEvent.path("actor").path("login").asText("Unknown user");
                String description = issueOrPr.path("body").asText("No description");
                if (description.length() > 200) {
                    description = description.substring(0, 200) + "...";
                }
                message = "New " + type + " | Title: " + title + " | User: " + user + " | Description: " + description;
            } else {
                message = "Unhandled event type: " + type;
            }
            return new Notifications(message, createdAt);
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
