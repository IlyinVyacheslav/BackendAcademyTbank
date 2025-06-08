package backend.academy.scrapper.clients.bot;

import backend.academy.dto.LinkUpdate;
import reactor.core.publisher.Mono;

public interface BotClient {
    Mono<String> postUpdates(LinkUpdate linkUpdate);
}
