package backend.academy.scrapper.exc;

public class ChatAlreadyExistsException extends BaseChatRepositoryException {
    public ChatAlreadyExistsException(String message) {
        super(message);
    }

    public ChatAlreadyExistsException(Long chatId) {
        super(String.format("Chat with linkId %d already exists", chatId));
    }
}
