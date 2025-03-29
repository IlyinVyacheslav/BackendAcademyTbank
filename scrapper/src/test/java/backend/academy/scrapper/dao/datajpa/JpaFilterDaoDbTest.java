package backend.academy.scrapper.dao.datajpa;

import backend.academy.scrapper.dao.AbstractFilterDaoDbTest;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = FilterDaoDataJpa.class)
@EnableJpaRepositories(basePackages = "backend.academy.scrapper.dao.datajpa.repo")
@EntityScan("backend.academy.scrapper.model.entity")
@ActiveProfiles("data-jpa")
public class JpaFilterDaoDbTest extends AbstractFilterDaoDbTest {}
