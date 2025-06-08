package backend.academy.scrapper.service;

import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.LinkResponse;
import backend.academy.dto.LinkUpdatedAt;
import backend.academy.dto.ListLinksResponse;
import backend.academy.dto.ListLinksUpdate;
import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.dao.ChatDao;
import backend.academy.scrapper.dao.FilterDao;
import backend.academy.scrapper.dao.LinkDao;
import backend.academy.scrapper.dao.TagDao;
import backend.academy.scrapper.exc.ChatAlreadyExistsException;
import backend.academy.scrapper.exc.ChatNotFoundException;
import backend.academy.scrapper.exc.InvalidNotificationModeException;
import backend.academy.scrapper.exc.LinkNotFoundException;
import backend.academy.scrapper.model.dto.Link;
import backend.academy.scrapper.service.digest.NotificationMode;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final ChatDao chatDao;
    private final LinkDao linkDao;
    private final TagDao tagDao;
    private final FilterDao filterDao;
    private final int pageSize;

    @Autowired
    public ChatService(ChatDao chatDao, LinkDao linkDao, TagDao tagDao, FilterDao filterDao, ScrapperConfig config) {
        this.chatDao = chatDao;
        this.linkDao = linkDao;
        this.tagDao = tagDao;
        this.filterDao = filterDao;
        this.pageSize = config.pageSize();
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

    public List<String> getLinksUrlsByTagFromChat(Long chatId, String tag) {
        return getLinksIdsByTagFromChat(chatId, tag).stream()
                .map(linkDao::getLinkUrlById)
                .toList();
    }

    public ListLinksUpdate getLinksUrlsByTagAndTimeFromChat(Long chatId, String tag, Timestamp fromTime) {
        List<LinkUpdatedAt> linkUpdatedAts = getLinksIdsByTagFromChat(chatId, tag).stream()
                .map(linkDao::getLinkById)
                .filter(link -> link.lastModified().after(fromTime))
                .map(link -> new LinkUpdatedAt(link.url(), link.lastModified()))
                .toList();
        return new ListLinksUpdate(linkUpdatedAts, linkUpdatedAts.size());
    }

    private List<Long> getLinksIdsByTagFromChat(Long chatId, String tag) {
        if (!chatDao.existsChat(chatId)) {
            throw new ChatNotFoundException(chatId);
        }
        return linkDao.findLinksByChatId(chatId).stream()
                .filter(linkId -> tagDao.existsTagByChatIdAndLinkIdAntTag(chatId, linkId, tag))
                .toList();
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

    public Stream<List<Link>> getAllLinksAsBatchStream() {
        return Stream.iterate(0, pageNumber -> pageNumber + 1)
                .map(pageNumber -> linkDao.getLinksPage(pageNumber, pageSize))
                .takeWhile(batch -> !batch.isEmpty());
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

    public List<String> getFiltersByChatIdAndLinkId(Long chatId, Long linkId) {
        if (!chatDao.existsChat(chatId)) {
            throw new ChatNotFoundException(chatId);
        }
        return filterDao.getFiltersByChatIdAndLinkId(chatId, linkId);
    }

    public NotificationMode getNotificationMode(Long chatId) {
        if (!chatDao.existsChat(chatId)) {
            throw new ChatNotFoundException(chatId);
        }
        return chatDao.getNotificationMode(chatId);
    }

    public void setNotificationMode(Long chatId, String notificationMode) {
        if (!chatDao.existsChat(chatId)) {
            throw new ChatNotFoundException(chatId);
        }
        try {
            NotificationMode notificationModeEnum = NotificationMode.valueOf(notificationMode);
            chatDao.setNotificationMode(chatId, notificationModeEnum);
        } catch (IllegalArgumentException e) {
            throw new InvalidNotificationModeException(notificationMode);
        }
    }
}
