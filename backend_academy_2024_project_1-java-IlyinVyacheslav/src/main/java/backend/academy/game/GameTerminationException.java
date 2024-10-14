package backend.academy.game;

public class GameTerminationException extends RuntimeException {
    public GameTerminationException(String message, Throwable cause) {
        super(message, cause);
    }

    public GameTerminationException(String message) {
        super(message);
    }
}
