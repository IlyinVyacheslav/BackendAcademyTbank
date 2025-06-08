package backend.academy.bot.service.commands;

import backend.academy.bot.clients.ScrapperClient;
import org.springframework.stereotype.Component;

@Component
public class HelpCommand implements Command {
    @Override
    public String getCommand() {
        return "/help";
    }

    @Override
    public String getDescription() {
        return "Доступные команды";
    }

    @Override
    public String execute(ScrapperClient scrapperClient, Long chatId) {
        return COMMANDS_LIST;
    }
}
