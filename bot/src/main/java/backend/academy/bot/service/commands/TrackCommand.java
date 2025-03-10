package backend.academy.bot.service.commands;

import backend.academy.bot.clients.ScrapperClient;
import org.springframework.stereotype.Component;

@Component
public class TrackCommand implements Command {
    @Override
    public boolean shouldBeReplied() {
        return true;
    }

    @Override
    public String getCommand() {
        return "/track";
    }

    @Override
    public String getDescription() {
        return "Добавить ссылку";
    }

    @Override
    public String execute(ScrapperClient scrapperClient, Long chatId) {
        return SEND_LINK_MESSAGE;
    }
}
