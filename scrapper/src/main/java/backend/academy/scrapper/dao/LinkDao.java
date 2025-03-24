package backend.academy.scrapper.dao;

import backend.academy.scrapper.model.dto.Link;
import java.sql.Timestamp;
import java.util.List;

public interface LinkDao {

    Long addLink(String url);

    void addLinkToChat(long chatId, long linkId);

    Long getLinkIdByUrl(String url);

    String getLinkUrlById(Long linkId);

    boolean removeLinkFromChatById(long chatId, long linkId);

    void updateLink(long linkId, Timestamp lastModified);

    List<Long> findLinksByChatId(Long chatId);

    List<Link> getAllLinks();

    List<Long> getAllChatIdsByLinkId(Long linkId);
}
