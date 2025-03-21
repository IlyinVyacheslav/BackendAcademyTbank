package backend.academy.scrapper.repository;

public interface ChatRepository {
    boolean existsChat(long chatId);

    void addChat(long charId);

    boolean removeChat(long chatId);
}
