package backend.academy.scrapper.dao.datajpa.repo;

import backend.academy.scrapper.model.entity.TagEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepo extends JpaRepository<TagEntity, Long> {
    List<TagEntity> findAllByChat_ChatIdAndLink_LinkId(Long chatId, Long linkId);

    boolean existsByChat_ChatIdAndLink_LinkIdAndTag(Long chatId, Long linkId, String tag);
}
