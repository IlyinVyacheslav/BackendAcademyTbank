package backend.academy.scrapper.dao.jdbc;

import backend.academy.scrapper.dao.TagDao;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Profile("jdbc")
@Repository
@RequiredArgsConstructor
public class TagDaoJdbc implements TagDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void addTag(Long chatId, Long linkId, String tag) {
        String sql = "INSERT INTO tags (chat_id, link_id, tag) VALUES (:chatId, :linkId, :tag)";
        jdbcTemplate.update(sql, Map.of("chatId", chatId, "linkId", linkId, "tag", tag));
    }

    @Override
    public List<String> getAllTagsByChatIdAndLinkId(Long chatId, Long linkId) {
        String sql = "SELECT tag FROM tags WHERE chat_id = :chatId AND link_id = :linkId";
        return jdbcTemplate.queryForList(sql, Map.of("chatId", chatId, "linkId", linkId), String.class);
    }

    @Override
    public boolean existsTagByChatIdAndLinkIdAntTag(Long chatId, Long linkId, String tag) {
        String sql = "SELECT EXISTS (SELECT * FROM tags WHERE chat_id = :chatId AND link_id = :linkId AND tag = :tag)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                sql, Map.of("chatId", chatId, "linkId", linkId, "tag", tag), Boolean.class));
    }

    @Override
    public void removeAllTagsFromChatByLinkId(Long chatId, Long linkId) {
        String sql = "DELETE FROM tags WHERE chat_id = :chatId AND link_id = :linkId";
        jdbcTemplate.update(sql, Map.of("chatId", chatId, "linkId", linkId));
    }
}
