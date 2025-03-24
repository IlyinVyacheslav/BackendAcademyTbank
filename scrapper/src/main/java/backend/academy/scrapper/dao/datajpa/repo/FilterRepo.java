package backend.academy.scrapper.dao.datajpa.repo;

import backend.academy.scrapper.model.entity.FilterEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilterRepo extends JpaRepository<FilterEntity, Long> {
    List<FilterEntity> getAllByChat_ChatIdAndLink_LinkId(Long chatId, Long linkId);
}
