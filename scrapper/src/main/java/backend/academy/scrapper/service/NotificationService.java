package backend.academy.scrapper.service;

import backend.academy.dto.LinkUpdate;
import backend.academy.logger.LoggerHelper;
import backend.academy.scrapper.clients.Notifications;
import backend.academy.scrapper.clients.bot.BotClient;
import backend.academy.scrapper.clients.web.GitHubClient;
import backend.academy.scrapper.clients.web.StackOverflowClient;
import backend.academy.scrapper.model.dto.Link;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final ChatService chatService;
    private final GitHubClient gitHubClient;
    private final StackOverflowClient stackOverflowClient;
    private final BotClient botClient;

    @Scheduled(fixedRate = 120_000)
    public void checkNotifications() {
        LoggerHelper.info("Checking notifications");
        chatService.getAllLinksAsBatchStream().forEach(linkEntityList -> {
            try (ForkJoinPool pool = new ForkJoinPool(4)) {
                pool.submit(() -> linkEntityList.parallelStream().forEach(this::processLink))
                        .get();
            } catch (Exception e) {
                LoggerHelper.error("Ошибка в параллелой обработке ссылок", e);
            }
        });
    }

    private void processLink(Link link) {
        LoggerHelper.info("Polling notifications for link" + link.url());
        Timestamp lastModified = link.lastModified();
        if (link.url().startsWith("github")) {
            gitHubClient
                    .getNewNotifications(link.url(), lastModified)
                    .doOnError(error -> LoggerHelper.error("Error while polling github", error))
                    .doOnSuccess(resp -> handleResponse(link, resp, lastModified))
                    .subscribe();
        } else if (link.url().startsWith("stackoverflow")) {
            stackOverflowClient
                    .getNewNotifications(link.url(), lastModified)
                    .doOnError(error -> LoggerHelper.error("Error while polling stackoverflow", error))
                    .doOnSuccess(resp -> handleResponse(link, resp, lastModified))
                    .subscribe();
        }
    }

    private void handleResponse(Link link, Notifications resp, Timestamp lastModified) {
        Timestamp updatedAt = resp.updatedAt();
        if (updatedAt == null || updatedAt.equals(lastModified)) {
            LoggerHelper.info("No updates happened", Map.of("response", resp));
        } else {
            try {
                chatService.updateLinkLastModifiedAt(link.id(), updatedAt);
                List<Long> chatsWithLinkId = chatService.getAllChatIdsByLinkId(link.id());
                List<Long> filteredChats = chatsWithLinkId.stream()
                        .filter(chatId -> resp.user()
                                .map(user -> {
                                    List<String> filters = chatService.getFiltersByChatIdAndLinkId(chatId, link.id());
                                    return !filters.contains(user);
                                })
                                .orElse(true))
                        .toList();

                botClient
                        .postUpdates(new LinkUpdate(link.id(), link.url(), resp.message(), filteredChats))
                        .doOnError(error -> LoggerHelper.error("Error while sending update to tgBot", error))
                        .doOnSuccess(res -> LoggerHelper.info("Update sent successfully", Map.of("response", res)))
                        .subscribe();
            } catch (Exception e) {
                LoggerHelper.error("Error while updating and sending link", Map.of("link", link.url()), e);
            }
        }
    }
}
