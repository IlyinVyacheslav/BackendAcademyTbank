package backend.academy.scrapper.repository.jdbc;

import backend.academy.scrapper.repository.TagRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TagRepositoryJDBC implements TagRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void addTag(Long chatId, Long linkId, String tag) {
        String sql = "INSERT INTO tags (chat_id, link_id, tag) VALUES (:chatId, :linkId, :tag)";
        jdbcTemplate.update(sql, Map.of("chatId", chatId, "linkId", linkId, "tag", tag));
    }

    @Override
    public List<String> getTagsByChatIdAndLinkId(Long chatId, Long linkId) {
        String sql = "SELECT tag FROM tags WHERE chat_id = :chatId AND link_id = :linkId";
        return jdbcTemplate.queryForList(sql, Map.of("chatId", chatId, "linkId", linkId), String.class);
    }

    @Override
    public void removeTag(Long chatId, Long linkId, String tag) {
        String sql = "DELETE FROM tags WHERE chat_id = :chatId AND link_id = :linkId AND tag = :tag";
        jdbcTemplate.update(sql, Map.of("chatId", chatId, "linkId", linkId, "tag", tag));
    }
}
