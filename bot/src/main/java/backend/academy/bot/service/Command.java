package backend.academy.bot.service;

import backend.academy.bot.exceptions.IllegalCommandException;
import lombok.Getter;

@Getter
public enum Command {
    START("/start"),
    HELP("/help"),
    LIST("/list"),
    TRACK("/track"),
    UNTRACK("/untrack");
    private final String command;

    Command(String command) {
        this.command = command;
    }

    public static Command getCommand(String chatId, String commandText) {
        for (Command command : values()) {
            if (command.command().equals(commandText)) {
                return command;
            }
        }
        throw new IllegalCommandException(chatId);
    }
}
