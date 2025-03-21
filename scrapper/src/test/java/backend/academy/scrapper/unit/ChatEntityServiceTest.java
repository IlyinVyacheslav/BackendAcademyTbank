package backend.academy.scrapper.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.ListLinksResponse;
import backend.academy.scrapper.exc.ChatAlreadyExistsException;
import backend.academy.scrapper.exc.ChatNotFoundException;
import backend.academy.scrapper.exc.LinkAlreadyExistsException;
import backend.academy.scrapper.exc.LinkNotFoundException;
import backend.academy.scrapper.model.Chat;
import backend.academy.scrapper.model.LinkInfo;
import backend.academy.scrapper.model.dto.Link;
import backend.academy.scrapper.repository.MapChatRepository;
import backend.academy.scrapper.repository.OldLinkRepository;
import backend.academy.scrapper.service.ChatService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatEntityServiceTest {
    private final Long chatId = 1L;
    private final String linkUrl = "http://example.com";
    private final Long linkId = 100L;

    @Mock
    private MapChatRepository mapChatRepository;

    @Mock
    private OldLinkRepository oldLinkRepository;

    @InjectMocks
    private ChatService chatService;

    private Chat chat;
    private Link link;
    private LinkInfo linkInfo;

    @BeforeEach
    void setUp() {
        link = new Link(linkId, linkUrl, null);
        linkInfo = new LinkInfo(linkId, Collections.emptyList(), Collections.emptyList());
        chat = new Chat(chatId, new ArrayList<>(List.of(linkInfo)));
    }

    @Test
    void registerChat_ShouldRegisterChat_WhenChatDoesNotExist() {
        when(mapChatRepository.getChat(chatId)).thenReturn(null);

        chatService.registerChat(chatId);

        verify(mapChatRepository, times(1)).addChat(any(Chat.class));
    }

    @Test
    void registerChat_ShouldThrowException_WhenChatAlreadyExists() {
        when(mapChatRepository.getChat(chatId)).thenReturn(chat);

        assertThatThrownBy(() -> chatService.registerChat(chatId)).isInstanceOf(ChatAlreadyExistsException.class);

        verify(mapChatRepository, never()).addChat(any(Chat.class));
    }

    @Test
    void deleteChat_ShouldDeleteChat_WhenChatExists() {
        when(mapChatRepository.removeChat(chatId)).thenReturn(true);

        chatService.deleteChat(chatId);

        verify(mapChatRepository, times(1)).removeChat(chatId);
    }

    @Test
    void deleteChat_ShouldThrowException_WhenChatDoesNotExist() {
        when(mapChatRepository.removeChat(chatId)).thenReturn(false);

        assertThatThrownBy(() -> chatService.deleteChat(chatId)).isInstanceOf(ChatNotFoundException.class);
    }

    @Test
    void getAllLinksFromChat_ShouldReturnLinks_WhenChatExists() {
        when(mapChatRepository.getChat(chatId)).thenReturn(chat);
        when(oldLinkRepository.getLink(linkId)).thenReturn(link);

        ListLinksResponse response = chatService.getAllLinksFromChat(chatId);

        assertThat(1).isEqualTo(response.size());
        assertThat(1).isEqualTo(response.links().size());
        assertThat(linkUrl).isEqualTo(response.links().getFirst().url());
    }

    @Test
    void getAllLinksFromChat_ShouldThrowException_WhenChatNotFound() {
        when(mapChatRepository.getChat(chatId)).thenReturn(null);

        assertThatThrownBy(() -> chatService.getAllLinksFromChat(chatId)).isInstanceOf(ChatNotFoundException.class);
    }

    @Test
    void addLinkToChat_ShouldAddLink_WhenNewLink() {
        Link newLink = new Link(101L, "https://new_example.com", null);
        when(mapChatRepository.getChat(chatId)).thenReturn(chat);
        when(oldLinkRepository.getLinkByUrl(newLink.url())).thenReturn(null);
        when(oldLinkRepository.addLink(newLink.url())).thenReturn(newLink);

        chatService.addLinkToChat(
                chatId, new AddLinkRequest(newLink.url(), Collections.emptyList(), Collections.emptyList()));

        verify(mapChatRepository, times(1)).updateChat(eq(chatId), any(Chat.class));
    }

    @Test
    void addLinkToChat_ShouldThrowException_WhenLinkAlreadyExists() {
        when(mapChatRepository.getChat(chatId)).thenReturn(chat);
        when(oldLinkRepository.getLinkByUrl(linkUrl)).thenReturn(link);

        assertThatThrownBy(() -> chatService.addLinkToChat(
                        chatId, new AddLinkRequest(linkUrl, Collections.emptyList(), Collections.emptyList())))
                .isInstanceOf(LinkAlreadyExistsException.class)
                .hasMessageContaining(chatId.toString(), linkUrl);
    }

    @Test
    void addLinkToChat_ShouldThrowException_WhenChatNotFound() {
        when(mapChatRepository.getChat(chatId)).thenReturn(null);

        assertThatThrownBy(() -> chatService.addLinkToChat(
                        chatId, new AddLinkRequest(linkUrl, Collections.emptyList(), Collections.emptyList())))
                .isInstanceOf(ChatNotFoundException.class);
    }

    @Test
    void deleteLinkFromChat_ShouldDeleteLink_WhenLinkExists() {
        when(mapChatRepository.getChat(chatId)).thenReturn(chat);
        when(oldLinkRepository.getLinkByUrl(linkUrl)).thenReturn(link);
        when(mapChatRepository.removeLinkFromChatById(chatId, linkId)).thenReturn(true);

        chatService.deleteLinkFromChat(chatId, link.url());

        verify(mapChatRepository, times(1)).removeLinkFromChatById(chatId, linkId);
    }

    @Test
    void deleteLinkFromChat_ShouldThrowException_WhenChatDoesNotExist() {
        when(mapChatRepository.getChat(chatId)).thenReturn(null);

        assertThatThrownBy(() -> chatService.deleteLinkFromChat(chatId, linkUrl))
                .isInstanceOf(ChatNotFoundException.class);
    }

    @Test
    void deleteLinkFromChat_ShouldThrowException_WhenLinkDoesNotExist() {
        when(mapChatRepository.getChat(chatId)).thenReturn(chat);
        when(oldLinkRepository.getLinkByUrl(linkUrl)).thenReturn(null);

        assertThatThrownBy(() -> chatService.deleteLinkFromChat(chatId, linkUrl))
                .isInstanceOf(LinkNotFoundException.class);
    }

    @Test
    void deleteLinkFromChat_ShouldThrowException_WhenLinkNotInChat() {
        when(mapChatRepository.getChat(chatId)).thenReturn(chat);
        when(oldLinkRepository.getLinkByUrl(linkUrl)).thenReturn(link);
        when(mapChatRepository.removeLinkFromChatById(chatId, linkId)).thenReturn(false);

        assertThatThrownBy(() -> chatService.deleteLinkFromChat(chatId, linkUrl))
                .isInstanceOf(LinkNotFoundException.class)
                .hasMessageContaining(chatId.toString(), linkUrl);
    }

    @Test
    void getAllLinks() {
        List<Link> expectedLinks = List.of(link);
        when(oldLinkRepository.getAllLinks()).thenReturn(expectedLinks);

        List<Link> links = chatService.getAllLinks();

        assertThat(expectedLinks).isEqualTo(links);
    }

    @Test
    void updateLinkLastModified_ShouldThrowException_WhenLinkDoesNotExist() {
        when(oldLinkRepository.getLink(linkId)).thenReturn(null);

        assertThatThrownBy(() -> chatService.updateLinkLastModifiedAt(linkId, null))
                .isInstanceOf(LinkNotFoundException.class);
    }
}
