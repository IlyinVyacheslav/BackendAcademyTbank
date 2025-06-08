package backend.academy.scrapper.dao;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.scrapper.service.digest.NotificationMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractChatDaoDbTest extends DbTest {
    @Autowired
    protected ChatDao chatDao;

    private long chatId = 387464794;

    @Test
    @DisplayName("Успешное создание чата")
    void testAddChat() {
        chatDao.addChat(chatId);
        boolean chatExists = chatDao.existsChat(chatId);

        assertThat(chatExists).isTrue();
    }

    @Test
    @DisplayName("Успешное удаление чата")
    void testRemoveChat() {
        chatDao.addChat(chatId);

        chatDao.removeChat(chatId);
        boolean chatExists = chatDao.existsChat(chatId);

        assertThat(chatExists).isFalse();
    }

    @Test
    @DisplayName("Успеное получение типа получения уведомлений по умолчанию")
    void testGetNotificationMode() {
        chatDao.addChat(chatId);

        NotificationMode notificationMode = chatDao.getNotificationMode(chatId);

        assertThat(notificationMode).isEqualTo(NotificationMode.IMMEDIATE);
    }

    @Test
    @DisplayName("Успеное изменение типа получения уведомлений")
    void testSetNotificationMode() {
        chatDao.addChat(chatId);

        chatDao.setNotificationMode(chatId, NotificationMode.DIGEST);
        NotificationMode notificationMode = chatDao.getNotificationMode(chatId);

        assertThat(notificationMode).isEqualTo(NotificationMode.DIGEST);
    }
}
