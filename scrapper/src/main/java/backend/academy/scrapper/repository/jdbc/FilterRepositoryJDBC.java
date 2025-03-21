package backend.academy.scrapper.repository.jdbc;

import backend.academy.scrapper.repository.FilterRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FilterRepositoryJDBC implements FilterRepository {
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
    public void removeFilter(Long chatId, Long linkId, String filter) {
        String sql = "DELETE FROM filters WHERE chat_id = :chatId AND link_id = :linkId AND filter = :filter";
        jdbcTemplate.update(sql, Map.of("chatId", chatId, "linkId", linkId, "filter", filter));
    }
}
