package backend.academy.scrapper.repository;

import backend.academy.scrapper.model.Chat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import org.springframework.stereotype.Repository;

@Repository
public class ChatRepository {
    private final Map<Long, Chat> CHATS = new ConcurrentHashMap<>();

    public Stream<Chat> getAllChatsAsStream() {
        return CHATS.values().stream();
    }

    public Chat getChat(long chatId) {
        return CHATS.get(chatId);
    }

    public void addChat(Chat chat) {
        CHATS.putIfAbsent(chat.chatId(), chat);
    }

    public boolean removeChat(long chatId) {
        return CHATS.remove(chatId) != null;
    }

    public void updateChat(long chatId, Chat chat) {
        CHATS.put(chatId, chat);
    }

    public boolean removeLinkFromChatById(long chatId, long linkId) {
        return getChat(chatId).removeLink(linkId);
    }
}
