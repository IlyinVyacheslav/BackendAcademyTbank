package backend.academy.scrapper.dao.datajpa.repo;

import backend.academy.scrapper.model.entity.LinkEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkRepo extends JpaRepository<LinkEntity, Long> {
    Optional<LinkEntity> findByUrl(String url);

    @Query("SELECT l.linkId FROM LinkEntity l JOIN l.chats c WHERE c.chatId = :chatId")
    List<Long> findLinkIdsByChatId(@Param("chatId") Long chatId);
}
