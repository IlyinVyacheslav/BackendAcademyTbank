package backend.academy.scrapper.dao;

import java.util.List;

public interface TagDao {
    void addTag(Long chatId, Long linkId, String tag);

    List<String> getAllTagsByChatIdAndLinkId(Long chatId, Long linkId);

    void removeAllTagsFromChatByLinkId(Long chatId, Long linkId);
}
