package backend.academy.scrapper.dao.jdbc;

import backend.academy.scrapper.dao.AbstractChatDaoDbTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = ChatDaoJdbc.class)
@EnableJpaRepositories
@ActiveProfiles("jdbc")
public class JdbcChatDaoDbTest extends AbstractChatDaoDbTest {}
