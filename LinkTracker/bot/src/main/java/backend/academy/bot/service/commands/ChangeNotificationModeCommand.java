package backend.academy.bot.service.commands;

import backend.academy.bot.clients.ScrapperClient;

public class ChangeNotificationModeCommand implements Command {
    @Override
    public String getCommand() {
        return "/changeNotificationMode";
    }

    @Override
    public String getDescription() {
        return "Изменение режима получения уведомлений";
    }

    @Override
    public String execute(ScrapperClient scrapperClient, Long chatId) {

        return UPDATE_NOTIFICATION_MODE_MESSAGE;
    }
}
