package backend.academy.scrapper.service;

import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.LinkResponse;
import backend.academy.dto.ListLinksResponse;
import backend.academy.scrapper.exc.LinkAlreadyExistsException;
import backend.academy.scrapper.exc.LinkNotFoundException;
import backend.academy.scrapper.model.Chat;
import backend.academy.scrapper.model.Link;
import backend.academy.scrapper.model.LinkInfo;
import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.repository.LinkRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final LinkRepository linkRepository;

    @Autowired
    public ChatService(ChatRepository chatRepository, LinkRepository linkRepository) {
        this.chatRepository = chatRepository;
        this.linkRepository = linkRepository;
    }

    public void registerChat(Long chatId) {
        chatRepository.addChat(new Chat(chatId));
    }

    public void deleteChat(Long chatId) {
        chatRepository.removeChat(chatId);
    }

    public ListLinksResponse getAllLinksFromChat(Long chatId) {
        List<LinkInfo> linkInfoList = chatRepository.getAllLinksFromChat(chatId);
        List<LinkResponse> linkResponses = linkInfoList.stream()
                .map(linkInfo -> {
                    Link link = linkRepository.getLink(linkInfo.linkId());
                    return new LinkResponse(link.id(), link.url(), linkInfo.tags(), linkInfo.filters());
                })
                .collect(Collectors.toList());
        return new ListLinksResponse(linkResponses, linkResponses.size());
    }

    public void addLinkToChat(Long chatId, AddLinkRequest addLinkRequest) {
        String url = addLinkRequest.link();
        Link link = linkRepository.getLinkByUrl(url);
        if (link == null) {
            link = linkRepository.addLink(url);
        }

        Chat chat = chatRepository.getChat(chatId);
        if (chat.containsUrl(link.id())) {
            throw new LinkAlreadyExistsException(chatId, url);
        }

        chat.addLink(new LinkInfo(link.id(), addLinkRequest.tags(), addLinkRequest.filters()));

        chatRepository.updateChat(chatId, chat);
    }

    public void deleteLinkFromChat(Long chatId, String url) {
        Link link = linkRepository.getLinkByUrl(url);
        if (link == null) {
            throw new LinkNotFoundException(url);
        }
        if (!chatRepository.removeLinkFromChatById(chatId, link.id())) {
            throw new LinkNotFoundException(chatId, url);
        }
    }

    public List<Link> getAllLinks() {
        return linkRepository.getAllLinks();
    }

    public void updateLinkLastModifiedAt(Long id, String modifiedAt) {
        Link oldLink = linkRepository.getLink(id);
        linkRepository.updateLink(new Link(oldLink.id(), oldLink.url(), modifiedAt));
    }

    public List<Long> getAllChatIdsByLinkId(Long linkId) {
        return chatRepository
                .getAllChatsAsStream()
                .filter(chat -> chat.containsUrl(linkId))
                .map(Chat::chatId)
                .collect(Collectors.toList());
    }
}
