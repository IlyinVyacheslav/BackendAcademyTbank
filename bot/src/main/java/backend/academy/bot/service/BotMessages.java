package backend.academy.bot.service;

public interface BotMessages {
    String SEND_LINK_MESSAGE = "Отправьте ссылку, которую хотите отслеживать:";
    String UNTRACK_LINK_MESSAGE = "Отправьте ссылку, которую больше не хотите отслеживать:";
    String SEND_TAGS_MESSAGE = "Введите теги (опционально: введите - если тега нет)";
    String NO_TAGS_SENT = "Не введены теги.";
    String SEND_FILTERS_MESSAGE = "Настройте фильтры (опционально: введите - если тега нет)";
    String NO_FILTERS_SENT = "Не введены фильтры.";
    String SEND_TAG_MESSAGE = "Введите тег:";
    String SEND_TAG_AND_TIME_MESSAGE = "Введите тег и время в формате: 2021-01-01T00:00:00 через пробел";
    String COMMANDS_LIST =
            """
        /start - start bot
        /track - choose url to follow
        /untrack - stop following linkEntity
        /list - all following links
        /linksByTag - list of links by tag
        /linksByTagAndTime - list of links by tag from time
        """;
    String UNKNOWN_COMMAND = "Неизвестная команда. Введите /help";
    String EMPTY_LIST_MESSAGE = "Вы пока ничего не отслеживаете";
}
