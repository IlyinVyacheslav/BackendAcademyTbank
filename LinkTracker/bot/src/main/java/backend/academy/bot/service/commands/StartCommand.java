package backend.academy.bot.service.commands;

import backend.academy.bot.clients.ScrapperClient;
import backend.academy.logger.LoggerHelper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class StartCommand implements Command {
    @Override
    public String getCommand() {
        return "/start";
    }

    @Override
    public String getDescription() {
        return "Запустить бота";
    }

    @Override
    public String execute(ScrapperClient scrapperClient, Long chatId) {
        return scrapperClient
                .registerChat(chatId)
                .onErrorResume(error -> {
                    LoggerHelper.error("Error while starting telegram", error);
                    return Mono.error(error);
                })
                .block(timeout);
    }
}
