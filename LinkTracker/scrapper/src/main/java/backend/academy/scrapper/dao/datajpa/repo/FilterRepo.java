package backend.academy.scrapper.dao.datajpa.repo;

import backend.academy.scrapper.model.entity.FilterEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FilterRepo extends JpaRepository<FilterEntity, Long> {
    List<FilterEntity> getAllByChat_ChatIdAndLink_LinkId(Long chatId, Long linkId);

    @Query(value = "SELECT f.filter FROM FilterEntity f WHERE f.chat.chatId = :chatId AND f.link.linkId = :linkId")
    List<String> findFilterValuesByChatIdAndLinkId(Long chatId, Long linkId);

    void deleteByChat_ChatIdAndLink_LinkId(Long chatId, Long linkId);
}
