package backend.academy.scrapper.dao.jdbc;

import backend.academy.scrapper.dao.ChatDao;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Profile("jdbc")
@Repository
@RequiredArgsConstructor
public class ChatDaoJdbc implements ChatDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public boolean existsChat(long chatId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM chats WHERE chat_id = :chatId)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Map.of("chatId", chatId), Boolean.class));
    }

    @Override
    public void addChat(long chatId) {
        String sql = "INSERT INTO chats (chat_id) VALUES (:chatId)";
        jdbcTemplate.update(sql, Map.of("chatId", chatId));
    }

    @Override
    public boolean removeChat(long chatId) {
        String sql = "DELETE FROM chats WHERE chat_id = :chatId";
        return jdbcTemplate.update(sql, Map.of("chatId", chatId)) > 0;
    }
}
