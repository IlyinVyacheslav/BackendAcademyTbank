package backend.academy.scrapper.dao;

public interface ChatDao {
    boolean existsChat(long chatId);

    void addChat(long charId);

    boolean removeChat(long chatId);
}
