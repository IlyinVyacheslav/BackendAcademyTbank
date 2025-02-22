package backend.academy.scrapper.exc;

public class BaseChatRepositoryException extends RuntimeException {
    public BaseChatRepositoryException(String message) {
        super(message);
    }

    public BaseChatRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
