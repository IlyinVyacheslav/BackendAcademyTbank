package backend.academy.scrapper.unit;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.scrapper.clients.Notifications;
import backend.academy.scrapper.clients.web.GitHubClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;

public class GitHubClientTest {
    @Mock
    private WebClient webClient;

    @InjectMocks
    private GitHubClient gitHubClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessResponse_PushEvent() throws Exception {
        String jsonResponse =
                """
            [{
                "type": "PushEvent",
                "created_at": "2024-03-29T12:34:56Z",
                "payload": {
                    "commits": [{
                        "message": "Fix bug",
                        "author": { "name": "JohnDoe" }
                    }]
                }
            }]
        """;
        JsonNode response = objectMapper.readTree(jsonResponse);
        Timestamp lastModified = Timestamp.from(Instant.now());

        Notifications notifications = gitHubClient.processResponse(response, lastModified);

        assertThat(notifications.message()).isEqualTo("New push by JohnDoe | Commit: Fix bug");
    }

    @Test
    void testProcessResponse_IssuesEvent() throws Exception {
        String jsonResponse =
                """
            [{
                "type": "IssuesEvent",
                "created_at": "2024-03-29T12:34:56Z",
                "actor": { "login": "JaneDoe" },
                "payload": {
                    "issue": {
                        "title": "Bug Report",
                        "body": "There is a bug in the system."
                    }
                }
            }]
        """;
        JsonNode response = objectMapper.readTree(jsonResponse);
        Timestamp lastModified = Timestamp.from(Instant.now());

        Notifications notifications = gitHubClient.processResponse(response, lastModified);

        assertThat(notifications.message())
                .isEqualTo(
                        "New IssuesEvent | Title: Bug Report | User: JaneDoe | Description: There is a bug in the system.");
    }

    @Test
    void testProcessResponse_NoNewEvents() throws Exception {
        String jsonResponse = "[]";
        JsonNode response = objectMapper.readTree(jsonResponse);
        Timestamp lastModified = Timestamp.from(Instant.now());

        Notifications notifications = gitHubClient.processResponse(response, lastModified);

        assertThat(notifications.message()).isEqualTo("No new events");
    }
}
