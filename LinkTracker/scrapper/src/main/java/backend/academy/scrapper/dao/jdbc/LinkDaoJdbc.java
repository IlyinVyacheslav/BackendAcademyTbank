package backend.academy.scrapper.dao.jdbc;

import backend.academy.scrapper.dao.LinkDao;
import backend.academy.scrapper.model.dto.Link;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Profile("SQL")
@Repository
@RequiredArgsConstructor
public class LinkDaoJdbc implements LinkDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Long addLink(String url) {
        String sql = "INSERT INTO links (url, last_modified) VALUES (:url, NOW()) RETURNING link_id";
        return jdbcTemplate.queryForObject(sql, Map.of("url", url), Long.class);
    }

    @Override
    public void addLinkToChat(long chatId, long linkId) {
        String sql = "INSERT INTO chat_links (chat_id, link_id) VALUES (:chatId, :linkId)";
        jdbcTemplate.update(sql, Map.of("chatId", chatId, "linkId", linkId));
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
        List<String> result = jdbcTemplate.query(sql, Map.of("linkId", linkId), (rs, rowNum) -> rs.getString("url"));
        return result.isEmpty() ? null : result.getFirst();
    }

    @Override
    public Link getLinkById(Long linkId) {
        String sql = "SELECT link_id, url, last_modified FROM links WHERE link_id = :linkId";
        return jdbcTemplate.queryForObject(
                sql,
                Map.of("linkId", linkId),
                (rs, rowNum) -> new Link(rs.getLong("link_id"), rs.getString("url"), rs.getTimestamp("last_modified")));
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
    public List<Link> getLinksPage(int pageNumber, int pageSize) {
        String sql = "SELECT link_id, url, last_modified FROM links LIMIT :limit OFFSET :offset";
        return jdbcTemplate.query(
                sql,
                Map.of("limit", pageSize, "offset", pageNumber * pageSize),
                (rs, rowNum) -> new Link(rs.getLong("link_id"), rs.getString("url"), rs.getTimestamp("last_modified")));
    }

    @Override
    public List<Long> getAllChatIdsByLinkId(Long linkId) {
        String sql = "SELECT chat_id FROM chat_links WHERE link_id = :linkId";
        return jdbcTemplate.queryForList(sql, Map.of("linkId", linkId), Long.class);
    }
}
