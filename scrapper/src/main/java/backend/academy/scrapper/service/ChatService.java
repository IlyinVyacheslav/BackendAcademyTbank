package backend.academy.scrapper.service;

import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.LinkResponse;
import backend.academy.dto.ListLinksResponse;
import backend.academy.scrapper.exc.ChatAlreadyExistsException;
import backend.academy.scrapper.exc.ChatNotFoundException;
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
        if (chatRepository.getChat(chatId) != null) {
            throw new ChatAlreadyExistsException(chatId);
        }
        chatRepository.addChat(new Chat(chatId));
    }

    public void deleteChat(Long chatId) {
        if (!chatRepository.removeChat(chatId)) {
            throw new ChatNotFoundException(chatId);
        }
    }

    public ListLinksResponse getAllLinksFromChat(Long chatId) {
        Chat chat = chatRepository.getChat(chatId);
        if (chat == null) {
            throw new ChatNotFoundException(chatId);
        }
        List<LinkResponse> linkResponses = chat.linksToFollow().stream()
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
        if (chat == null) {
            throw new ChatNotFoundException(chatId);
        }

        if (chat.containsUrl(link.id())) {
            throw new LinkAlreadyExistsException(chatId, url);
        }

        chat.addLink(new LinkInfo(link.id(), addLinkRequest.tags(), addLinkRequest.filters()));

        chatRepository.updateChat(chatId, chat);
    }

    public void deleteLinkFromChat(Long chatId, String url) {
        if (chatRepository.getChat(chatId) == null) {
            throw new ChatNotFoundException(chatId);
        }
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
        if (oldLink == null) {
            throw new LinkNotFoundException(String.format("There is no link with id %d", id));
        }
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
