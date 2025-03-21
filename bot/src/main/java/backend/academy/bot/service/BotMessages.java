package backend.academy.bot.service;

public interface BotMessages {
    String SEND_LINK_MESSAGE = "Отправьте ссылку, которую хотите отслеживать:";
    String UNTRACK_LINK_MESSAGE = "Отправьте ссылку, которую больше не хотите отслеживать:";
    String SEND_TAGS_MESSAGE = "Введите теги (опционально)";
    String SEND_FILTERS_MESSAGE = "Настройте фильтры (опционально)";
    String COMMANDS_LIST =
            """
        /start - start bot
        /track - choose url to follow
        /untrack - stop following linkEntity
        /list - all following links
        """;
    String UNKNOWN_COMMAND = "Неизвестная команда. Введите /help";
    String EMPTY_LIST_MESSAGE = "Вы пока ничего не отслеживаете";
}
