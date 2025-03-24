package backend.academy.scrapper.dao.jdbc;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.scrapper.dao.TagDao;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = TagDaoJdbc.class)
public class TagDaoDbTest extends DbTest {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private TagDao tagDao;

    private long chatId = 387464794;
    private long linkId = 1;
    private String tag = "tag";

    @BeforeEach
    public void createChatAndLink() {
        jdbcTemplate.update("INSERT INTO chats (chat_id) VALUES (:chatId)", Map.of("chatId", chatId));
        jdbcTemplate.update(
                "INSERT INTO links (url) VALUES ('https://www.google.com/')", EmptySqlParameterSource.INSTANCE);
    }

    @Test
    @DisplayName("Успешное добавление тега")
    void testAddTag() {
        tagDao.addTag(chatId, linkId, tag);

        List<String> tags = tagDao.getAllTagsByChatIdAndLinkId(chatId, linkId);
        assertThat(tags).containsExactly(tag);
    }

    @Test
    @DisplayName("Успешное удаление фильтров")
    void testRemoveAllTagsFromChatByLinkId() {
        List<String> tags = List.of("tag1", "tag2", "tag3");
        tags.forEach(tag -> tagDao.addTag(chatId, linkId, tag));

        tagDao.removeAllTagsFromChatByLinkId(chatId, linkId);

        List<String> result = tagDao.getAllTagsByChatIdAndLinkId(chatId, linkId);
        assertThat(result).isEmpty();
    }
}
