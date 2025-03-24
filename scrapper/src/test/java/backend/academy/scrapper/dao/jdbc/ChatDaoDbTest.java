package backend.academy.scrapper.dao.jdbc;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.scrapper.dao.ChatDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = ChatDaoJdbc.class)
public class ChatDaoDbTest extends DbTest {
    @Autowired
    private ChatDao chatDao;

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
