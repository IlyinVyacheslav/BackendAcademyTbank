package backend.academy.bot.exceptions;

public class InvalidTagAndTimeFormatException extends RuntimeException {
    public InvalidTagAndTimeFormatException(String message) {
        super(message);
    }
}
