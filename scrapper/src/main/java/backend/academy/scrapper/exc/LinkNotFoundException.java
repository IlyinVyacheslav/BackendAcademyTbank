package backend.academy.scrapper.exc;

public class LinkNotFoundException extends BaseChatRepositoryException {
    public LinkNotFoundException(String url) {
        super(String.format("There is no link found for url %s", url));
    }

    public LinkNotFoundException(Long chatId, String url) {
        super(String.format("There is no link with url: %s in chat with id: %d", url, chatId));
    }
}
