package backend.academy.bot.exceptions;

public class IllegalCommandException extends IllegalArgumentException {
    public IllegalCommandException(String message) {
        super(message);
    }
}
