package backend.academy.scrapper.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public abstract class AbstractFilterDaoDbTest extends DbTest {
    @Autowired
    protected NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    protected FilterDao filterDao;

    private long chatId = 387464794;
    private long linkId = 1;
    private String filter = "filter";

    @BeforeEach
    public void createChatAndLink() {
        jdbcTemplate.update(
                "INSERT INTO chats (chat_id, notification_mode) VALUES (:chatId, 'IMMEDIATE')",
                Map.of("chatId", chatId));
        jdbcTemplate.update(
                "INSERT INTO links (link_id, url) VALUES (1, 'https://www.google.com/')",
                EmptySqlParameterSource.INSTANCE);
    }

    @Test
    @DisplayName("Успешное добавление фильтра")
    void testAddFilter() {
        filterDao.addFilter(chatId, linkId, filter);

        List<String> filters = filterDao.getFiltersByChatIdAndLinkId(chatId, linkId);
        assertThat(filters).containsExactly(filter);
    }

    @Test
    @DisplayName("Успешное удаление фильтров")
    void testRemoveAllFiltersFromChatByLinkId() {
        List<String> filters = List.of("filter1", "filter2", "filter3");
        filters.forEach(filter -> filterDao.addFilter(chatId, linkId, filter));

        filterDao.removeAllFiltersFromChatByLinkId(chatId, linkId);

        List<String> result = filterDao.getFiltersByChatIdAndLinkId(chatId, linkId);
        assertThat(result).isEmpty();
    }
}
