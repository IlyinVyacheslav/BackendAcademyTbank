package backend.academy.scrapper.repository;

import backend.academy.scrapper.model.dto.Link;
import java.sql.Timestamp;
import java.util.List;

public interface LinkRepository {

    Long addLink(long chatId, String url);

    Long getLinkIdByUrl(String url);

    String getLinkUrlById(Long linkId);

    boolean removeLinkFromChatById(long chatId, long linkId);

    void updateLink(long linkId, Timestamp lastModified);

    List<Long> findLinksByChatId(Long chatId);

    List<Link> getAllLinks();

    List<Long> getAllChatIdsByLinkId(Long linkId);
}
