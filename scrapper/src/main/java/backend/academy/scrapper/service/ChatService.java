package backend.academy.scrapper.service;

import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.LinkResponse;
import backend.academy.dto.ListLinksResponse;
import backend.academy.scrapper.dao.ChatDao;
import backend.academy.scrapper.dao.FilterDao;
import backend.academy.scrapper.dao.LinkDao;
import backend.academy.scrapper.dao.TagDao;
import backend.academy.scrapper.exc.ChatAlreadyExistsException;
import backend.academy.scrapper.exc.ChatNotFoundException;
import backend.academy.scrapper.exc.LinkNotFoundException;
import backend.academy.scrapper.model.dto.Link;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final ChatDao chatDao;
    private final LinkDao linkDao;
    private final TagDao tagDao;
    private final FilterDao filterDao;

    @Autowired
    public ChatService(ChatDao chatDao, LinkDao linkDao, TagDao tagDao, FilterDao filterDao) {
        this.chatDao = chatDao;
        this.linkDao = linkDao;
        this.tagDao = tagDao;
        this.filterDao = filterDao;
    }

    public void registerChat(Long chatId) {
        if (chatDao.existsChat(chatId)) {
            throw new ChatAlreadyExistsException(chatId);
        }
        chatDao.addChat(chatId);
    }

    public void deleteChat(Long chatId) {
        if (!chatDao.removeChat(chatId)) {
            throw new ChatNotFoundException(chatId);
        }
    }

    public ListLinksResponse getAllLinksFromChat(Long chatId) {
        if (!chatDao.existsChat(chatId)) {
            throw new ChatNotFoundException(chatId);
        }
        List<Long> linkIds = linkDao.findLinksByChatId(chatId);
        List<LinkResponse> linkResponses = linkIds.stream()
                .map(linkId -> {
                    List<String> tags = tagDao.getAllTagsByChatIdAndLinkId(chatId, linkId);
                    List<String> filters = filterDao.getFiltersByChatIdAndLinkId(chatId, linkId);
                    return new LinkResponse(linkId, linkDao.getLinkUrlById(linkId), tags, filters);
                })
                .collect(Collectors.toList());
        return new ListLinksResponse(linkResponses, linkResponses.size());
    }

    public void addLinkToChat(Long chatId, AddLinkRequest linkRequest) {
        if (!chatDao.existsChat(chatId)) {
            throw new ChatNotFoundException(chatId);
        }
        Long linkId = linkDao.getLinkIdByUrl(linkRequest.link());
        if (linkId == null) {
            linkId = linkDao.addLink(linkRequest.link());
        }
        linkDao.addLinkToChat(chatId, linkId);

        Long finalLinkId = linkId;
        linkRequest.tags().forEach(tag -> tagDao.addTag(chatId, finalLinkId, tag));
        linkRequest.filters().forEach(filter -> filterDao.addFilter(chatId, finalLinkId, filter));
    }

    public void deleteLinkFromChat(Long chatId, String url) {
        if (!chatDao.existsChat(chatId)) {
            throw new ChatNotFoundException(chatId);
        }

        Long linkId = linkDao.getLinkIdByUrl(url);
        if (linkId == null) {
            throw new LinkNotFoundException(url);
        }

        if (!linkDao.removeLinkFromChatById(chatId, linkId)) {
            throw new LinkNotFoundException(chatId, url);
        }

        tagDao.removeAllTagsFromChatByLinkId(chatId, linkId);
        filterDao.removeAllFiltersFromChatByLinkId(chatId, linkId);
    }

    public List<Link> getAllLinks() {
        return linkDao.getAllLinks();
    }

    public void updateLinkLastModifiedAt(Long linkId, Timestamp modifiedAt) {
        if (linkDao.getLinkUrlById(linkId) == null) {
            throw new LinkNotFoundException(String.format("There is no link with linkId %d", linkId));
        }
        linkDao.updateLink(linkId, modifiedAt);
    }

    public List<Long> getAllChatIdsByLinkId(Long linkId) {
        return linkDao.getAllChatIdsByLinkId(linkId);
    }
}
