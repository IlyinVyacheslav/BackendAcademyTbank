package backend.academy.scrapper.clients;

import reactor.core.publisher.Mono;

public interface WebSiteClient {
    Mono<Notifications> getNewNotifications(String url, String lastModified);
}
