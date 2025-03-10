package backend.academy.scrapper.exc;

public class LinkAlreadyExistsException extends BaseChatRepositoryException {
    public LinkAlreadyExistsException(String message) {
        super(message);
    }

    public LinkAlreadyExistsException(Long chatId, String url) {
        super(String.format("Chat with id:%d already follows url:%s", chatId, url));
    }
}
