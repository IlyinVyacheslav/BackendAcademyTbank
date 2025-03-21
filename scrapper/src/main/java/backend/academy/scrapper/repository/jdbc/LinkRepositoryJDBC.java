package backend.academy.scrapper.repository.jdbc;

import backend.academy.scrapper.model.dto.Link;
import backend.academy.scrapper.repository.LinkRepository;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LinkRepositoryJDBC implements LinkRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Long addLink(long chatId, String url) {
        String sql = "INSERT INTO links (url, last_modified) VALUES (:url, NOW()) RETURNING link_id";
        Long linkId = jdbcTemplate.queryForObject(sql, Map.of("url", url), Long.class);

        sql = "INSERT INTO chat_links (chat_id, link_id) VALUES (:chatId, :linkId)";
        jdbcTemplate.update(sql, Map.of("chatId", chatId, "linkId", linkId));

        return linkId;
    }

    @Override
    public Long getLinkIdByUrl(String url) {
        String sql = "SELECT link_id FROM links WHERE url = :url";
        try {
            return jdbcTemplate.queryForObject(sql, Map.of("url", url), Long.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public String getLinkUrlById(Long linkId) {
        String sql = "SELECT url FROM links WHERE link_id = :linkId";
        return jdbcTemplate.queryForObject(sql, Map.of("linkId", linkId), String.class);
    }

    @Override
    public boolean removeLinkFromChatById(long chatId, long linkId) {
        String sql = "DELETE FROM chat_links WHERE chat_id = :chatId AND link_id = :linkId";
        return jdbcTemplate.update(sql, Map.of("chatId", chatId, "linkId", linkId)) > 0;
    }

    @Override
    public void updateLink(long linkId, Timestamp lastModified) {
        String sql = "UPDATE links SET last_modified = :lastModified WHERE link_id = :linkId";
        jdbcTemplate.update(sql, Map.of("linkId", linkId, "lastModified", lastModified));
    }

    @Override
    public List<Long> findLinksByChatId(Long chatId) {
        String sql = "SELECT link_id FROM chat_links WHERE chat_id = :chatId";
        return jdbcTemplate.queryForList(sql, Map.of("chatId", chatId), Long.class);
    }

    @Override
    public List<Link> getAllLinks() {
        String sql = "SELECT link_id, url, last_modified FROM links";
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Link(rs.getLong("link_id"), rs.getString("url"), rs.getTimestamp("last_modified")));
    }

    @Override
    public List<Long> getAllChatIdsByLinkId(Long linkId) {
        String sql = "SELECT chat_id FROM chat_links WHERE link_id = :linkId";
        return jdbcTemplate.queryForList(sql, Map.of("linkId", linkId), Long.class);
    }
}
