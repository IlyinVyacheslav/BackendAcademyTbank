package backend.academy.bot.service.commands;

import backend.academy.bot.clients.ScrapperClient;
import org.springframework.stereotype.Component;

@Component
public class UrlsByTagCommand implements Command {
    @Override
    public boolean shouldBeReplied() {
        return true;
    }

    @Override
    public String getCommand() {
        return "/linksByTag";
    }

    @Override
    public String getDescription() {
        return "Список отслеживаемых ссылок по тегу";
    }

    @Override
    public String execute(ScrapperClient scrapperClient, Long chatId) {
        return SEND_TAG_MESSAGE;
    }
}
