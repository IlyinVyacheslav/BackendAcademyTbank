package backend.academy.scrapper.clients.web;

import backend.academy.scrapper.clients.Notifications;
import java.sql.Timestamp;
import reactor.core.publisher.Mono;

public interface WebSiteClient {
    Mono<Notifications> getNewNotifications(String url, Timestamp lastModified);
}
