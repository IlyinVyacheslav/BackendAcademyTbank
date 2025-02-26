package backend.academy.bot.service;

import backend.academy.bot.exceptions.IllegalCommandException;
import com.pengrad.telegrambot.model.BotCommand;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Command {
    START("/start", "Запустить бота"),
    HELP("/help", "Доступные команды"),
    LIST("/list", "Список отслеживаемых ссылок"),
    TRACK("/track", "Добавить ссылку"),
    UNTRACK("/untrack", "Удалить ссылку");
    private final String command;
    private final String description;

    Command(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public static Command getCommand(String chatId, String commandText) {
        for (Command command : values()) {
            if (command.command().equals(commandText)) {
                return command;
            }
        }
        throw new IllegalCommandException(chatId);
    }

    public static BotCommand[] getAllCommands() {
        return Arrays.stream(values())
                .map(cmd -> new BotCommand(cmd.command(), cmd.description()))
                .toArray(BotCommand[]::new);
    }
}
