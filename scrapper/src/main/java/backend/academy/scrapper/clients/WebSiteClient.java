package backend.academy.scrapper.clients;

import java.sql.Timestamp;
import reactor.core.publisher.Mono;

public interface WebSiteClient {
    Mono<Notifications> getNewNotifications(String url, Timestamp lastModified);
}
