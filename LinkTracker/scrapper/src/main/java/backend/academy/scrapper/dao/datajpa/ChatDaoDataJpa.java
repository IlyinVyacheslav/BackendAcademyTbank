package backend.academy.scrapper.dao.datajpa;

import backend.academy.scrapper.dao.ChatDao;
import backend.academy.scrapper.dao.datajpa.repo.ChatRepo;
import backend.academy.scrapper.model.entity.ChatEntity;
import backend.academy.scrapper.service.digest.NotificationMode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Profile("ORM")
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
        Long removedChats = chatRepo.deleteById(chatId);
        return removedChats != null && removedChats != 0;
    }

    @Override
    public NotificationMode getNotificationMode(long chatId) {
        return chatRepo.findById(chatId).map(ChatEntity::notificationMode).orElse(NotificationMode.IMMEDIATE);
    }

    @Override
    public void setNotificationMode(long chatId, NotificationMode notificationMode) {
        chatRepo.findById(chatId).ifPresent(chat -> {
            chat.notificationMode(notificationMode);
            chatRepo.save(chat);
        });
    }
}
