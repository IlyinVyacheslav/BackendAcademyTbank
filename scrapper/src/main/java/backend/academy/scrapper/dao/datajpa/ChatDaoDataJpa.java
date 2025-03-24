package backend.academy.scrapper.dao.datajpa;

import backend.academy.scrapper.dao.ChatDao;
import backend.academy.scrapper.dao.datajpa.repo.ChatRepo;
import backend.academy.scrapper.model.entity.ChatEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Profile("data-jpa")
@Repository
@RequiredArgsConstructor
public class ChatDaoDataJpa implements ChatDao {
    private final ChatRepo chatRepo;

    @Override
    public boolean existsChat(long chatId) {
        return chatRepo.existsChatByChatId(chatId);
    }

    @Override
    public void addChat(long chatId) {
        ChatEntity newChat = new ChatEntity();
        newChat.chatId(chatId);
        chatRepo.saveAndFlush(newChat);
    }

    @Override
    public boolean removeChat(long chatId) {
        if (!chatRepo.existsChatByChatId(chatId)) return false;
        chatRepo.deleteById(chatId);
        return true;
    }
}
