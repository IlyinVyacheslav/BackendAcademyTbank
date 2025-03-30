package backend.academy.scrapper.dao.jdbc;

import backend.academy.scrapper.dao.FilterDao;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Profile("SQL")
@Repository
@RequiredArgsConstructor
public class FilterDaoJdbc implements FilterDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void addFilter(Long chatId, Long linkId, String filter) {
        String sql = "INSERT INTO filters (chat_id, link_id, filter) VALUES (:chatId, :linkId, :filter)";
        jdbcTemplate.update(sql, Map.of("chatId", chatId, "linkId", linkId, "filter", filter));
    }

    @Override
    public List<String> getFiltersByChatIdAndLinkId(Long chatId, Long linkId) {
        String sql = "SELECT filter FROM filters WHERE chat_id = :chatId AND link_id = :linkId";
        return jdbcTemplate.queryForList(sql, Map.of("chatId", chatId, "linkId", linkId), String.class);
    }

    @Override
    public void removeAllFiltersFromChatByLinkId(Long chatId, Long linkId) {
        String sql = "DELETE FROM filters WHERE chat_id = :chatId AND link_id = :linkId";
        jdbcTemplate.update(sql, Map.of("chatId", chatId, "linkId", linkId));
    }
}
