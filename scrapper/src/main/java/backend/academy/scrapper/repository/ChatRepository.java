package backend.academy.scrapper.repository;

import backend.academy.scrapper.exc.ChatNotFoundException;
import backend.academy.scrapper.exc.IdOccupiedException;
import backend.academy.scrapper.model.Chat;
import backend.academy.scrapper.model.LinkInfo;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import org.springframework.stereotype.Repository;

@Repository
public class ChatRepository {
    private final Map<Long, Chat> CHATS = new ConcurrentHashMap<>();

    {
        CHATS.put(1L, new Chat(1L));
    }

    public Stream<Chat> getAllChatsAsStream() {
        return CHATS.values().stream();
    }

    public Chat getChat(long chatId) {
        if (hasChatWithId(chatId)) {
            return CHATS.get(chatId);
        }
        throw new ChatNotFoundException(chatId);
    }

    public void addChat(Chat chat) {
        if (hasChatWithId(chat.chatId())) {
            throw new IdOccupiedException(String.format("Chat with id: %d already exists", chat.chatId()));
        }
        CHATS.putIfAbsent(chat.chatId(), chat);
    }

    public boolean removeChat(long chatId) {
        if (hasChatWithId(chatId)) {
            CHATS.remove(chatId);
        }
        throw new ChatNotFoundException(chatId);
    }

    public void updateChat(long chatId, Chat chat) {
        CHATS.put(chatId, chat);
    }

    /*    public void addLinkToChat(long chatId, LinkInfo link) {
        if (!getChat(chatId).addLink(link)) {
            throw new LinkAlreadyExistsException(chatId, link.url());
        }
    }*/

    public boolean removeLinkFromChatById(long chatId, long linkId) {
        return getChat(chatId).removeLink(linkId);
    }

    public List<LinkInfo> getAllLinksFromChat(long chatId) {
        return getChat(chatId).linksToFollow();
    }

    private boolean hasChatWithId(long chatId) {
        return CHATS.containsKey(chatId);
    }
}
