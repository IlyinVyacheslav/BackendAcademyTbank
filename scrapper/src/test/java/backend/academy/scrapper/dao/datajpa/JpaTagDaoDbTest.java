package backend.academy.scrapper.dao.datajpa;

import backend.academy.scrapper.dao.AbstractTagDaoDbTest;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = TagDaoDataJpa.class)
@EnableJpaRepositories(basePackages = "backend.academy.scrapper.dao.datajpa.repo")
@EntityScan("backend.academy.scrapper.model.entity")
@ActiveProfiles("ORM")
public class JpaTagDaoDbTest extends AbstractTagDaoDbTest {}
