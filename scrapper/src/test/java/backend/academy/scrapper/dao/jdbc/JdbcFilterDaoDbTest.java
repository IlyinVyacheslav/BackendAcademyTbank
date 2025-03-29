package backend.academy.scrapper.dao.jdbc;

import backend.academy.scrapper.dao.AbstractFilterDaoDbTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = FilterDaoJdbc.class)
@EnableJpaRepositories
@ActiveProfiles("jdbc")
public class JdbcFilterDaoDbTest extends AbstractFilterDaoDbTest {}
