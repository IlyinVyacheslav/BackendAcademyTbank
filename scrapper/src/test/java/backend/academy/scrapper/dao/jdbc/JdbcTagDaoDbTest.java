package backend.academy.scrapper.dao.jdbc;

import backend.academy.scrapper.dao.AbstractTagDaoDbTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = TagDaoJdbc.class)
@EnableJpaRepositories
@ActiveProfiles("jdbc")
public class JdbcTagDaoDbTest extends AbstractTagDaoDbTest {}
