package backend.academy.scrapper.dao;

import java.util.List;

public interface FilterDao {
    void addFilter(Long chatId, Long linkId, String filter);

    List<String> getFiltersByChatIdAndLinkId(Long chatId, Long linkId);

    void removeAllFiltersFromChatByLinkId(Long chatId, Long linkId);
}
