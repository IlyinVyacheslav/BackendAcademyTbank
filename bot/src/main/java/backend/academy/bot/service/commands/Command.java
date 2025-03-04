package backend.academy.bot.service.commands;

import backend.academy.bot.clients.ScrapperClient;
import backend.academy.bot.service.BotMessages;
import java.time.Duration;

public interface Command extends BotMessages {
    Duration timeout = Duration.ofSeconds(5);

    String getCommand();

    String getDescription();

    String execute(ScrapperClient scrapperClient, Long chatId);

    default boolean shouldBeReplied() {
        return false;
    }
}
