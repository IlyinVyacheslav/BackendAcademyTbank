package backend.academy.scrapper.service;

import backend.academy.dto.LinkUpdate;
import backend.academy.logger.LoggerHelper;
import backend.academy.scrapper.clients.BotClient;
import backend.academy.scrapper.clients.GitHubClient;
import backend.academy.scrapper.clients.Notifications;
import backend.academy.scrapper.clients.StackOverflowClient;
import backend.academy.scrapper.model.dto.Link;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
        LoggerHelper.info("Checking notifications");
        chatService.getAllLinks().forEach(linkEntity -> {
            Timestamp lastModified = linkEntity.lastModified();
            if (linkEntity.url().startsWith("github")) {
                gitHubClient
                        .getNewNotifications(linkEntity.url(), lastModified)
                        .doOnError(error -> LoggerHelper.error("Error while polling github", error))
                        .doOnSuccess(resp -> handleResponse(linkEntity, resp, lastModified))
                        .subscribe();
            } else if (linkEntity.url().startsWith("stackoverflow")) {
                stackOverflowClient
                        .getNewNotifications(linkEntity.url(), lastModified)
                        .doOnError(error -> LoggerHelper.error("Error while polling stackoverflow", error))
                        .doOnSuccess(resp -> handleResponse(linkEntity, resp, lastModified))
                        .subscribe();
            }
        });
    }

    private void handleResponse(Link link, Notifications resp, Timestamp lastModified) {
        Timestamp updatedAt = resp.updatedAt();
        if (updatedAt == null || updatedAt.equals(lastModified)) {
            LoggerHelper.info("No updates happened", Map.of("response", resp));
        } else {
            try {
                chatService.updateLinkLastModifiedAt(link.linkId(), updatedAt);
                List<Long> chatsWithLinkId = chatService.getAllChatIdsByLinkId(link.linkId());
                botClient
                        .postUpdates(new LinkUpdate(link.linkId(), link.url(), resp.message(), chatsWithLinkId))
                        .doOnError(error -> LoggerHelper.error("Error while sending update to tgBot", error))
                        .doOnSuccess(res -> LoggerHelper.info("Update sent successfully", Map.of("response", res)))
                        .subscribe();
            } catch (Exception e) {
                LoggerHelper.error("Error while updating and sending link", Map.of("link", link.url()), e);
            }
        }
    }
}
