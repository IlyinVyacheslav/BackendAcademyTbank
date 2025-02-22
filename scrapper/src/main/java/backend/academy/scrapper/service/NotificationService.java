package backend.academy.scrapper.service;

import backend.academy.dto.Update;
import backend.academy.scrapper.clients.BotClient;
import backend.academy.scrapper.clients.GitHubClient;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {
    private final ChatService chatService;
    private final GitHubClient gitHubClient;
    private final BotClient botClient;

    @Autowired
    public NotificationService(ChatService chatService, GitHubClient gitHubClient, BotClient botClient) {
        this.chatService = chatService;
        this.gitHubClient = gitHubClient;
        this.botClient = botClient;
    }

    @Scheduled(fixedRate = 120_000)
    public void checkNotifications() {
        log.info("Checking notifications...");
        chatService.getAllLinks().forEach(link -> {
            String lastModified = link.lastModified();
            if (link.url().startsWith("github")) {
                gitHubClient
                        .getNewNotifications(link.url(), lastModified)
                        .doOnError(error -> log.error("Error while polling github", error))
                        .doOnSuccess(resp -> {
                            String updatedAt = resp.updatedAt();
                            if (updatedAt == null || updatedAt.equals(lastModified)) {
                                log.info("Response from github client {}", resp);
                            } else {
                                chatService.updateLinkLastModifiedAt(link.id(), updatedAt);
                                List<Long> chatsWithLinkId = chatService.getAllChatIdsByLinkId(link.id());
                                botClient
                                        .postUpdates(new Update(link.id(), link.url(), resp.message(), chatsWithLinkId))
                                        .doOnError(error -> log.error("Error while sending update to tgBot:", error))
                                        .doOnSuccess(res -> log.info("Update sent successfully: {}", res))
                                        .subscribe();
                            }
                        })
                        .subscribe();
            }
        });
    }
}
