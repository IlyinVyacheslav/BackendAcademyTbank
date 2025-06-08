package backend.academy.scrapper.dao;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.scrapper.model.dto.Link;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public abstract class AbstractLinkDaoDbTest extends DbTest {
    @Autowired
    protected NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    protected LinkDao linkDao;

    private String url = "https://www.google.com";
    private long chatId = 387464794;

    private void addChatToDb(long chatId) {
        jdbcTemplate.update(
                "INSERT INTO chats (chat_id, notification_mode) VALUES (:chatId, 'IMMEDIATE')",
                Map.of("chatId", chatId));
    }

    private Long addLinkAndChatToDb() {
        addChatToDb(chatId);
        return linkDao.addLink(url);
        //        jdbcTemplate.update("INSERT INTO links (url) VALUES (:url)", Map.of("url", url));
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
        Long linkId = linkDao.addLink(url);
        Long actualLinkId = linkDao.getLinkIdByUrl(url);

        assertThat(actualLinkId).isEqualTo(linkId);
    }

    @Test
    @DisplayName("Успешное добавление ссылки в чат")
    void testAddLinkToChat() {
        Long likId = addLinkAndChatToDb();

        linkDao.addLinkToChat(chatId, likId);

        assertThat(getNumberOfEntriesIn_chat_links(likId)).isEqualTo(1);
    }

    @Test
    @DisplayName("Успешное получение ссылки по id")
    void testGetLinkUrlById() {
        Long linkId = linkDao.addLink(url);

        String actualUrl = linkDao.getLinkUrlById(linkId);

        assertThat(actualUrl).isEqualTo(url);
    }

    @Test
    @DisplayName("Успешное удаление ссылки из чата")
    void testRemoveLinkFromChat() {
        Long linkId = addLinkAndChatToDb();

        linkDao.removeLinkFromChatById(chatId, linkId);

        assertThat(getNumberOfEntriesIn_chat_links(linkId)).isZero();
    }

    @Test
    @DisplayName("Успешное обновление ссылки")
    void testUpdateLink() {
        Timestamp lastModified = Timestamp.valueOf("2021-01-01 00:00:00.000000");
        Long linkId = linkDao.addLink(url);

        linkDao.updateLink(linkId, lastModified);

        Timestamp actualLastModified = jdbcTemplate.queryForObject(
                "SELECT last_modified FROM links WHERE link_id = :linkId", Map.of("linkId", linkId), Timestamp.class);
        assertThat(linkDao.getLinkUrlById(linkId)).isEqualTo(url);
        assertThat(actualLastModified).isEqualTo(lastModified);
    }

    @Test
    @DisplayName("Успешное получение всех ссылок из чата")
    void testGetLinksPageByChatId() {
        addLinkAndChatToDb();
        List<String> links = List.of("https://www.yandex.ru", "https://www.mail.ru", "https://www.amazon.com");
        List<Long> expectedIndices =
                links.stream().map(link -> linkDao.addLink(link)).toList();

        links.forEach(link -> linkDao.addLinkToChat(chatId, linkDao.getLinkIdByUrl(link)));
        List<Long> actualLinks = linkDao.findLinksByChatId(chatId);

        assertThat(actualLinks).hasSize(3);
        assertThat(actualLinks).containsExactlyInAnyOrderElementsOf(expectedIndices);
    }

    @Test
    @DisplayName("Успешное получение всех ссылок")
    void testGetLinksPage_returnsAllLinks() {
        List<String> links = List.of("aaa.com", "bbb.com", "ccc.com", "ddd.com", "eee.com");

        links.forEach(linkDao::addLink);
        List<Link> allLinks = linkDao.getLinksPage(0, 5);

        assertThat(allLinks).hasSize(5);
        assertThat(allLinks).extracting(Link::url).containsExactlyElementsOf(links);
    }

    @Test
    @DisplayName("Успешное получание ссылок с пагинацией")
    void testGetLinksPage_returnsLinksFromPage() {
        List<String> links = List.of("aaa.com", "bbb.com", "ccc.com", "ddd.com", "eee.com");

        links.forEach(linkDao::addLink);
        List<Link> actualLinks = linkDao.getLinksPage(2, 2);

        assertThat(actualLinks).hasSize(1);
        assertThat(actualLinks).extracting(Link::url).containsExactlyElementsOf(links.subList(4, 5));
    }

    @Test
    @DisplayName("Получение пустого списка ссылок с пагинацией")
    void testGetLinksPage_returnsEmptyList() {
        List<String> links = List.of("aaa.com", "bbb.com", "ccc.com", "ddd.com", "eee.com");

        links.forEach(linkDao::addLink);
        List<Link> actualLinks = linkDao.getLinksPage(2, 3);

        assertThat(actualLinks).isEmpty();
    }

    @Test
    @DisplayName("Успешное получение всех чатов по ссылке")
    void testGetAllChatsByLinkId() {
        List<Long> chats = List.of(1L, 2L, 3L, 4L, 5L);
        Long linkId = linkDao.addLink(url);
        chats.forEach(id -> {
            addChatToDb(id);
            linkDao.addLinkToChat(id, linkId);
        });

        List<Long> actualChats = linkDao.getAllChatIdsByLinkId(linkId);

        assertThat(actualChats).containsExactlyInAnyOrderElementsOf(chats);
    }
}
