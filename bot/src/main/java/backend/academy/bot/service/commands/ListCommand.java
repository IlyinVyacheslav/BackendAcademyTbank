package backend.academy.bot.service.commands;

import backend.academy.bot.clients.ScrapperClient;
import backend.academy.logger.LoggerHelper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ListCommand implements Command {
    @Override
    public String getCommand() {
        return "/list";
    }

    @Override
    public String getDescription() {
        return "Список отслеживаемых ссылок";
    }

    @Override
    public String execute(ScrapperClient scrapperClient, Long chatId) {
        return scrapperClient
                .getAllLinks(chatId)
                .map(links -> {
                    if (links == null || links.isEmpty()) {
                        return EMPTY_LIST_MESSAGE;
                    } else {
                        return links.toString();
                    }
                })
                .onErrorResume(error -> {
                    LoggerHelper.error("Error while getting links", error);
                    return Mono.error(error);
                })
                .block(timeout);
    }
}
