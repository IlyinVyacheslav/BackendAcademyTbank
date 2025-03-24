package backend.academy.scrapper.dao.datajpa.repo;

import backend.academy.scrapper.model.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepo extends JpaRepository<ChatEntity, Long> {
    boolean existsChatByChatId(long chatId);
}
