package backend.academy.scrapper.dao;

import static org.assertj.core.api.Assertions.assertThat;

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
}
