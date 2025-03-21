package backend.academy.scrapper.repository;

import java.util.List;

public interface FilterRepository {
    void addFilter(Long chatId, Long linkId, String filter);

    List<String> getFiltersByChatIdAndLinkId(Long chatId, Long linkId);

    void removeFilter(Long chatId, Long linkId, String filter);
}
