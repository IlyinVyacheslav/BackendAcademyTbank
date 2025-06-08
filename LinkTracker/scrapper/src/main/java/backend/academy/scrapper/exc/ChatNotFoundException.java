package backend.academy.scrapper.exc;

public class ChatNotFoundException extends BaseChatRepositoryException {
    public ChatNotFoundException(Long id) {
        super(String.format("Chat with linkId: %d does not exist", id));
    }

    public ChatNotFoundException(String message) {
        super(message);
    }

    public ChatNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
