package backend.academy.bot.service;

import backend.academy.bot.exceptions.IllegalCommandException;

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

    public static Command getCommand(String commandText) {
        for (Command command : values()) {
            if (command.getCommand().equals(commandText)) {
                return command;
            }
        }
        throw new IllegalCommandException("Unknown command: " + commandText);
    }

    public String getCommand() {
        return command;
    }
}
