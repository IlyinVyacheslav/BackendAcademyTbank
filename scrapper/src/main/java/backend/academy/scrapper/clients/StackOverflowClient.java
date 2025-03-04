package backend.academy.scrapper.clients;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class StackOverflowClient extends AbstractWebClient {

    @Autowired
    public StackOverflowClient(WebClient stackOverflowWebClient) {
        super(stackOverflowWebClient);
    }

    @Override
    public Mono<Notifications> getNewNotifications(String url, String lastModified) {
        String questionId = parseStackOverflowUrl(url);
        if (questionId == null) {
            return Mono.just(new Notifications("Incorrect url:" + url, lastModified));
        }
        String uri = "/2.3/questions/" + questionId + "?order=desc&sort=activity&site=stackoverflow";
        return fetchNotifications(uri, lastModified);
    }

    @Override
    protected Notifications processResponse(JsonNode response, String lastModified) {
        JsonNode items = response.get("items");
        if (items != null && items.isArray() && !items.isEmpty()) {
            JsonNode latestQuestion = items.get(0);
            String lastActivityDate = latestQuestion.get("last_activity_date").asText();
            return new Notifications("New activity on question", lastActivityDate);
        }
        return new Notifications("No new activity", lastModified);
    }

    private String parseStackOverflowUrl(String url) {
        String[] parts = url.split("/");
        for (int i = 0; i < parts.length; i++) {
            if ("questions".equals(parts[i]) && i + 1 < parts.length) {
                return parts[i + 1];
            }
        }
        return null;
    }
}
