package backend.academy.scrapper.dao;

import backend.academy.scrapper.service.digest.NotificationMode;

public interface ChatDao {
    boolean existsChat(long chatId);

    void addChat(long charId);

    boolean removeChat(long chatId);

    NotificationMode getNotificationMode(long chatId);

    void setNotificationMode(long chatId, NotificationMode notificationMode);
}
