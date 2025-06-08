package backend.academy.scrapper.dao.jdbc;

import backend.academy.scrapper.dao.AbstractLinkDaoDbTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = LinkDaoJdbc.class)
@EnableJpaRepositories
@ActiveProfiles("SQL")
public class JdbcLinkDaoDbTest extends AbstractLinkDaoDbTest {}
