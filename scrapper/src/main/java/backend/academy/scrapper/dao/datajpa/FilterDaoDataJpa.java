package backend.academy.scrapper.dao.datajpa;

import backend.academy.scrapper.dao.FilterDao;
import backend.academy.scrapper.dao.datajpa.repo.FilterRepo;
import backend.academy.scrapper.model.entity.ChatEntity;
import backend.academy.scrapper.model.entity.FilterEntity;
import backend.academy.scrapper.model.entity.LinkEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Profile("ORM")
@Repository
@RequiredArgsConstructor
public class FilterDaoDataJpa implements FilterDao {
    private final FilterRepo filterRepo;

    @Override
    public void addFilter(Long chatId, Long linkId, String filter) {
        FilterEntity newFilter = new FilterEntity();
        newFilter.filter(filter);

        LinkEntity link = new LinkEntity();
        link.linkId(linkId);
        newFilter.link(link);

        ChatEntity chat = new ChatEntity();
        chat.chatId(chatId);
        newFilter.chat(chat);

        filterRepo.save(newFilter);
    }

    @Override
    public List<String> getFiltersByChatIdAndLinkId(Long chatId, Long linkId) {
        return filterRepo.findFilterValuesByChatIdAndLinkId(chatId, linkId);
    }

    @Override
    public void removeAllFiltersFromChatByLinkId(Long chatId, Long linkId) {
        filterRepo.deleteByChat_ChatIdAndLink_LinkId(chatId, linkId);
    }
}
