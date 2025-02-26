package backend.academy.scrapper.service;

import backend.academy.dto.LinkUpdate;
import backend.academy.scrapper.clients.BotClient;
import backend.academy.scrapper.clients.GitHubClient;
import backend.academy.scrapper.clients.Notifications;
import backend.academy.scrapper.clients.StackOverflowClient;
import backend.academy.scrapper.model.Link;
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
    private final StackOverflowClient stackOverflowClient;
    private final BotClient botClient;

    @Autowired
    public NotificationService(
            ChatService chatService,
            GitHubClient gitHubClient,
            StackOverflowClient stackOverflowClient,
            BotClient botClient) {
        this.chatService = chatService;
        this.gitHubClient = gitHubClient;
        this.stackOverflowClient = stackOverflowClient;
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
                        .doOnSuccess(resp -> handleResponse(link, resp, lastModified))
                        .subscribe();
            } else if (link.url().startsWith("stackoverflow")) {
                stackOverflowClient
                        .getNewNotifications(link.url(), lastModified)
                        .doOnError(error -> log.error("Error while polling stackoverflow", error))
                        .doOnSuccess(resp -> handleResponse(link, resp, lastModified))
                        .subscribe();
            }
        });
    }

    private void handleResponse(Link link, Notifications resp, String lastModified) {
        String updatedAt = resp.updatedAt();
        if (updatedAt == null || updatedAt.equals(lastModified)) {
            log.info("Response from github client {}", resp);
        } else {
            try {
                chatService.updateLinkLastModifiedAt(link.id(), updatedAt);
                List<Long> chatsWithLinkId = chatService.getAllChatIdsByLinkId(link.id());
                botClient
                        .postUpdates(new LinkUpdate(link.id(), link.url(), resp.message(), chatsWithLinkId))
                        .doOnError(error -> log.error("Error while sending update to tgBot:", error))
                        .doOnSuccess(res -> log.info("Update sent successfully: {}", res))
                        .subscribe();
            } catch (Exception e) {
                log.error("Error while updating and sending link: {}", link.url(), e);
            }
        }
    }
}
