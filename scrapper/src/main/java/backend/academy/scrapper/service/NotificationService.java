package backend.academy.scrapper.service;

import backend.academy.dto.LinkUpdate;
import backend.academy.logger.LoggerHelper;
import backend.academy.scrapper.clients.Notifications;
import backend.academy.scrapper.clients.bot.BotClient;
import backend.academy.scrapper.clients.web.GitHubClient;
import backend.academy.scrapper.clients.web.StackOverflowClient;
import backend.academy.scrapper.model.dto.Link;
import backend.academy.scrapper.service.digest.DigestStorage;
import backend.academy.scrapper.service.digest.NotificationMode;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
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
    private final DigestStorage digestStorage;

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

                Map<NotificationMode, List<Long>> chatsByMode =
                        filteredChats.stream().collect(Collectors.groupingBy(chatService::getNotificationMode));

                List<Long> immediateChats = chatsByMode.getOrDefault(NotificationMode.IMMEDIATE, List.of());
                if (!immediateChats.isEmpty()) {
                    botClient
                            .postUpdates(new LinkUpdate(link.id(), link.url(), resp.message(), immediateChats))
                            .doOnError(error -> LoggerHelper.error("Error while sending update to tgBot", error))
                            .doOnSuccess(res -> LoggerHelper.info("Update sent successfully", Map.of("response", res)))
                            .subscribe();
                }

                List<Long> digestChats = chatsByMode.getOrDefault(NotificationMode.DIGEST, List.of());
                if (!digestChats.isEmpty()) {
                    LinkUpdate digestUpdate = new LinkUpdate(link.id(), link.url(), resp.message(), digestChats);
                    digestStorage.addToDigest(digestUpdate);
                }
            } catch (Exception e) {
                LoggerHelper.error("Error while updating and sending link", Map.of("link", link.url()), e);
            }
        }
    }
}
