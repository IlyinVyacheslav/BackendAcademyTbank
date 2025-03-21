package backend.academy.scrapper.service;

import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.LinkResponse;
import backend.academy.dto.ListLinksResponse;
import backend.academy.scrapper.exc.ChatAlreadyExistsException;
import backend.academy.scrapper.exc.ChatNotFoundException;
import backend.academy.scrapper.exc.LinkNotFoundException;
import backend.academy.scrapper.model.dto.Link;
import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.repository.FilterRepository;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.repository.TagRepository;
import backend.academy.scrapper.repository.jdbc.FilterRepositoryJDBC;
import backend.academy.scrapper.repository.jdbc.LinkRepositoryJDBC;
import backend.academy.scrapper.repository.jdbc.TagRepositoryJDBC;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final LinkRepository linkRepositoryJDBC;
    private final TagRepository tagRepositoryJDBC;
    private final FilterRepository filterRepositoryJDBC;

    @Autowired
    public ChatService(
            ChatRepository chatRepository,
            LinkRepositoryJDBC linkRepositoryJDBC,
            TagRepositoryJDBC tagRepositoryJDBC,
            FilterRepositoryJDBC filterRepositoryJDBC) {
        this.chatRepository = chatRepository;
        this.linkRepositoryJDBC = linkRepositoryJDBC;
        this.tagRepositoryJDBC = tagRepositoryJDBC;
        this.filterRepositoryJDBC = filterRepositoryJDBC;
    }

    public void registerChat(Long chatId) {
        if (chatRepository.existsChat(chatId)) {
            throw new ChatAlreadyExistsException(chatId);
        }
        chatRepository.addChat(chatId);
    }

    public void deleteChat(Long chatId) {
        if (!chatRepository.removeChat(chatId)) {
            throw new ChatNotFoundException(chatId);
        }
    }

    public ListLinksResponse getAllLinksFromChat(Long chatId) {
        List<Long> linkIds = linkRepositoryJDBC.findLinksByChatId(chatId);
        List<LinkResponse> linkResponses = linkIds.stream()
                .map(linkId -> {
                    List<String> tags = tagRepositoryJDBC.getTagsByChatIdAndLinkId(chatId, linkId);
                    List<String> filters = filterRepositoryJDBC.getFiltersByChatIdAndLinkId(chatId, linkId);
                    return new LinkResponse(linkId, linkRepositoryJDBC.getLinkUrlById(linkId), tags, filters);
                })
                .collect(Collectors.toList());
        return new ListLinksResponse(linkResponses, linkResponses.size());
    }

    public void addLinkToChat(Long chatId, AddLinkRequest linkRequest) {
        if (!chatRepository.existsChat(chatId)) {
            throw new ChatNotFoundException(chatId);
        }
        Long linkId = linkRepositoryJDBC.getLinkIdByUrl(linkRequest.link());
        if (linkId == null) {
            linkId = linkRepositoryJDBC.addLink(chatId, linkRequest.link());
        }
        Long finalLinkId = linkId;
        linkRequest.tags().forEach(tag -> tagRepositoryJDBC.addTag(chatId, finalLinkId, tag));
        linkRequest.filters().forEach(filter -> filterRepositoryJDBC.addFilter(chatId, finalLinkId, filter));
    }

    public void deleteLinkFromChat(Long chatId, String url) {
        if (!chatRepository.existsChat(chatId)) {
            throw new ChatNotFoundException(chatId);
        }

        Long linkId = linkRepositoryJDBC.getLinkIdByUrl(url);
        if (linkId == null) {
            throw new LinkNotFoundException(url);
        }

        if (!linkRepositoryJDBC.removeLinkFromChatById(chatId, linkId)) {
            throw new LinkNotFoundException(chatId, url);
        }
    }

    public List<Link> getAllLinks() {
        return linkRepositoryJDBC.getAllLinks();
    }

    public void updateLinkLastModifiedAt(Long linkId, Timestamp modifiedAt) {
        if (linkRepositoryJDBC.getLinkUrlById(linkId) == null) {
            throw new LinkNotFoundException(String.format("There is no link with linkId %d", linkId));
        }
        linkRepositoryJDBC.updateLink(linkId, modifiedAt);
    }

    public List<Long> getAllChatIdsByLinkId(Long linkId) {
        return linkRepositoryJDBC.getAllChatIdsByLinkId(linkId);
    }
}
