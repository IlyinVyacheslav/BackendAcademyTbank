package backend.academy.bot.exceptions;

public class InvalidChatIdException extends RuntimeException {
    public InvalidChatIdException(String chatId) {
        super(String.format("Invalid chat ID: %s, expected Long", chatId));
    }
}
