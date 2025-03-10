package backend.academy.bot.service.commands;

import backend.academy.bot.clients.ScrapperClient;
import org.springframework.stereotype.Component;

@Component
public class UntrackCommand implements Command {
    @Override
    public boolean shouldBeReplied() {
        return true;
    }

    @Override
    public String getCommand() {
        return "/untrack";
    }

    @Override
    public String getDescription() {
        return "Удалить ссылку";
    }

    @Override
    public String execute(ScrapperClient scrapperClient, Long chatId) {
        return UNTRACK_LINK_MESSAGE;
    }
}
