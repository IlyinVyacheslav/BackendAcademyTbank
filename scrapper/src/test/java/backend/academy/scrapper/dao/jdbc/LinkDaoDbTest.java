package backend.academy.scrapper.dao.jdbc;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.scrapper.dao.LinkDao;
import backend.academy.scrapper.model.dto.Link;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = LinkDaoJdbc.class)
public class LinkDaoDbTest extends DbTest {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private LinkDao linkDao;

    private String url = "https://www.google.com";
    private long chatId = 387464794;

    private void addChatToDb(long chatId) {
        jdbcTemplate.update("INSERT INTO chats (chat_id) VALUES (:chatId)", Map.of("chatId", chatId));
    }

    private void addLinkAndChatToDb() {
        addChatToDb(chatId);
        jdbcTemplate.update("INSERT INTO links (url) VALUES (:url)", Map.of("url", url));
    }

    private @Nullable Integer getNumberOfEntriesIn_chat_links(long linkId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chat_links WHERE link_id = :linkId AND chat_id = :chatId",
                Map.of("linkId", linkId, "chatId", chatId),
                Integer.class);
    }

    @Test
    @DisplayName("Успешное создание ссылки")
    void testAddLink() {
        linkDao.addLink(url);
        Long actualLinkId = linkDao.getLinkIdByUrl(url);

        assertThat(actualLinkId).isEqualTo(1);
    }

    @Test
    @DisplayName("Успешное добавление ссылки в чат")
    void testAddLinkToChat() {
        addLinkAndChatToDb();

        linkDao.addLinkToChat(chatId, 1);

        assertThat(getNumberOfEntriesIn_chat_links(1)).isEqualTo(1);
    }

    @Test
    @DisplayName("Успешное получение ссылки по id")
    void testGetLinkUrlById() {
        linkDao.addLink(url);

        String actualUrl = linkDao.getLinkUrlById(1L);

        assertThat(actualUrl).isEqualTo(url);
    }

    @Test
    @DisplayName("Успешное удаление ссылки из чата")
    void testRemoveLinkFromChat() {
        addLinkAndChatToDb();

        linkDao.removeLinkFromChatById(chatId, 1);

        assertThat(getNumberOfEntriesIn_chat_links(1)).isZero();
    }

    @Test
    @DisplayName("Успешное обновление ссылки")
    void testUpdateLink() {
        Timestamp lastModified = Timestamp.valueOf("2021-01-01 00:00:00.000000");
        linkDao.addLink(url);

        linkDao.updateLink(1L, lastModified);

        Timestamp actualLastModified = jdbcTemplate.queryForObject(
                "SELECT last_modified FROM links WHERE link_id = :linkId", Map.of("linkId", 1L), Timestamp.class);
        assertThat(linkDao.getLinkUrlById(1L)).isEqualTo(url);
        assertThat(actualLastModified).isEqualTo(lastModified);
    }

    @Test
    @DisplayName("Успешное получение всех ссылок из чата")
    void testGetLinksByChatId() {
        addLinkAndChatToDb();
        List<String> links = List.of("https://www.yandex.ru", "https://www.mail.ru", "https://www.amazon.com");

        links.forEach(linkDao::addLink);
        links.forEach(link -> linkDao.addLinkToChat(chatId, linkDao.getLinkIdByUrl(link)));
        List<Long> actualLinks = linkDao.findLinksByChatId(chatId);

        assertThat(actualLinks).hasSize(3);
        assertThat(actualLinks).containsExactly(2L, 3L, 4L);
    }

    @Test
    @DisplayName("Успешное получение всех ссылок")
    void testGetAllLinks() {
        List<String> links = List.of("aaa.com", "bbb.com", "ccc.com", "ddd.com", "eee.com");

        links.forEach(linkDao::addLink);
        List<Link> allLinks = linkDao.getAllLinks();

        assertThat(allLinks).hasSize(5);
        assertThat(allLinks)
                .extracting(Link::url)
                .containsExactly("aaa.com", "bbb.com", "ccc.com", "ddd.com", "eee.com");
    }

    @Test
    @DisplayName("Успешное получение всех чатов по ссылке")
    void testGetAllChatsByLinkId() {
        List<Long> chats = List.of(1L, 2L, 3L, 4L, 5L);
        linkDao.addLink(url);
        chats.forEach(id -> {
            addChatToDb(id);
            linkDao.addLinkToChat(id, 1L);
        });

        List<Long> actualChats = linkDao.getAllChatIdsByLinkId(1L);

        assertThat(actualChats).containsExactlyInAnyOrderElementsOf(chats);
    }
}
