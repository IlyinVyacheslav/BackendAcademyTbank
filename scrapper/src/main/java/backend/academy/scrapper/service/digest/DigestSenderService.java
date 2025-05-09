package backend.academy.scrapper.service.digest;

import backend.academy.dto.LinkUpdate;
import backend.academy.logger.LoggerHelper;
import backend.academy.scrapper.clients.bot.BotClient;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DigestSenderService {
    private final DigestStorage digestSender;
    private final BotClient botClient;

    @Scheduled(cron = "${app.digest-frequency}")
    public void sendDigestNotifications() {
        List<LinkUpdate> linkUpdates = digestSender.getAndClearAllDigests();

        linkUpdates.forEach(linkUpdate -> botClient
                .postUpdates(linkUpdate)
                .doOnError(error -> LoggerHelper.error("Error while sending update digest to tgBot", error))
                .doOnSuccess(res -> LoggerHelper.info("Update digest sent successfully", Map.of("response", res)))
                .subscribe());
    }
}
