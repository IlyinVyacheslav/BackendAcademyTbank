package backend.academy.bot.service.commands;

import backend.academy.bot.clients.ScrapperClient;
import org.springframework.stereotype.Component;

@Component
public class UrlsByTagAndTimeCommand implements Command {
    @Override
    public String getCommand() {
        return "/linksByTagAndTime";
    }

    @Override
    public String getDescription() {
        return "Список отслеживаемых ссылок по тегу у которых случилось обновление";
    }

    @Override
    public String execute(ScrapperClient scrapperClient, Long chatId) {
        return SEND_TAG_AND_TIME_MESSAGE;
    }

    @Override
    public boolean shouldBeReplied() {
        return true;
    }
}
