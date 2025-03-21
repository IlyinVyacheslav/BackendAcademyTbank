package backend.academy.scrapper.repository;

import java.util.List;

public interface TagRepository {
    void addTag(Long chatId, Long linkId, String tag);

    List<String> getTagsByChatIdAndLinkId(Long chatId, Long linkId);

    void removeTag(Long chatId, Long linkId, String tag);
}
