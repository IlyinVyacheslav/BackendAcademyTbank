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
import backend.academy.scrapper.service.ChatService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
    @Mock
    private ChatDao chatDao;

    @Mock
    private LinkDao linkDao;

    @Mock
    private TagDao tagDao;

    @Mock
    private FilterDao filterDao;

    @InjectMocks
    private ChatService chatService;

    private final Long chatId = 1L;
    private final String linkUrl = "http://example.com";
    private final Long linkId = 100L;
    private Link link;
    private LinkResponse linkResponse;

    @BeforeEach
    void setUp() {
        link = new Link(linkId, linkUrl, null);
        linkResponse = new LinkResponse(linkId, linkUrl, List.of("tag"), List.of("filter"));
    }

    @Test
    void registerChat_ShouldRegisterChat_WhenChatDoesNotExist() {
        when(chatDao.existsChat(chatId)).thenReturn(false);

        chatService.registerChat(chatId);

        verify(chatDao, times(1)).addChat(chatId);
    }

    @Test
    void registerChat_ShouldThrowException_WhenChatAlreadyExists() {
        when(chatDao.existsChat(chatId)).thenReturn(true);

        assertThatThrownBy(() -> chatService.registerChat(chatId)).isInstanceOf(ChatAlreadyExistsException.class);

        verify(chatDao, never()).addChat(any(Long.class));
    }

    @Test
    void deleteChat_ShouldDeleteChat_WhenChatExists() {
        when(chatDao.removeChat(chatId)).thenReturn(true);

        chatService.deleteChat(chatId);

        verify(chatDao, times(1)).removeChat(chatId);
    }

    @Test
    void deleteChat_ShouldThrowException_WhenChatDoesNotExist() {
        when(chatDao.removeChat(chatId)).thenReturn(false);

        assertThatThrownBy(() -> chatService.deleteChat(chatId)).isInstanceOf(ChatNotFoundException.class);
    }

    @Test
    void getAllLinksFromChat_ShouldReturnLinks_WhenChatExists() {
        when(chatDao.existsChat(chatId)).thenReturn(true);
        when(linkDao.findLinksByChatId(chatId)).thenReturn(List.of(linkId));
        when(linkDao.getLinkUrlById(linkId)).thenReturn(linkUrl);
        when(filterDao.getFiltersByChatIdAndLinkId(chatId, linkId)).thenReturn(linkResponse.filters());
        when(tagDao.getAllTagsByChatIdAndLinkId(chatId, linkId)).thenReturn(linkResponse.tags());

        ListLinksResponse response = chatService.getAllLinksFromChat(chatId);

        assertThat(response.size()).isEqualTo(1);
        assertThat(response.links()).containsExactly(linkResponse);
    }

    @Test
    void getAllLinksFromChat_ShouldThrowException_WhenChatNotFound() {
        when(chatDao.existsChat(chatId)).thenReturn(false);

        assertThatThrownBy(() -> chatService.getAllLinksFromChat(chatId)).isInstanceOf(ChatNotFoundException.class);
    }

    @Test
    void addLinkToChat_ShouldAddLink_WhenNewLink() {
        Link newLink = new Link(101L, "https://new_example.com", null);
        when(chatDao.existsChat(chatId)).thenReturn(true);
        when(linkDao.getLinkIdByUrl(newLink.url())).thenReturn(null);
        when(linkDao.addLink(newLink.url())).thenReturn(newLink.id());

        chatService.addLinkToChat(
                chatId, new AddLinkRequest(newLink.url(), Collections.emptyList(), Collections.emptyList()));

        verify(tagDao, never()).addTag(any(Long.class), any(Long.class), any(String.class));
        verify(filterDao, never()).addFilter(any(Long.class), any(Long.class), any(String.class));
        verify(linkDao, times(1)).addLinkToChat(eq(chatId), eq(newLink.id()));
    }

    @Test
    void addLinkToChat_ShouldAddLink_WhenLinkAlreadyExists() {
        Link newLink = new Link(101L, "https://new_example.com", null);
        when(chatDao.existsChat(chatId)).thenReturn(true);
        when(linkDao.getLinkIdByUrl(newLink.url())).thenReturn(newLink.id());

        chatService.addLinkToChat(
                chatId, new AddLinkRequest(newLink.url(), Collections.emptyList(), Collections.emptyList()));

        verify(linkDao, never()).addLink(any(String.class));
        verify(tagDao, never()).addTag(any(Long.class), any(Long.class), any(String.class));
        verify(filterDao, never()).addFilter(any(Long.class), any(Long.class), any(String.class));
        verify(linkDao, times(1)).addLinkToChat(eq(chatId), eq(newLink.id()));
    }

    @Test
    void addLinkToChat_ShouldThrowException_WhenChatNotFound() {
        when(chatDao.existsChat(chatId)).thenReturn(false);

        assertThatThrownBy(() -> chatService.addLinkToChat(
                        chatId, new AddLinkRequest(linkUrl, Collections.emptyList(), Collections.emptyList())))
                .isInstanceOf(ChatNotFoundException.class);
    }

    @Test
    void deleteLinkFromChat_ShouldDeleteLink_WhenLinkExists() {
        when(chatDao.existsChat(chatId)).thenReturn(true);
        when(linkDao.getLinkIdByUrl(linkUrl)).thenReturn(linkId);
        when(linkDao.removeLinkFromChatById(chatId, linkId)).thenReturn(true);

        chatService.deleteLinkFromChat(chatId, link.url());

        verify(linkDao, times(1)).removeLinkFromChatById(chatId, linkId);
        verify(tagDao, times(1)).removeAllTagsFromChatByLinkId(chatId, linkId);
        verify(filterDao, times(1)).removeAllFiltersFromChatByLinkId(chatId, linkId);
    }

    @Test
    void deleteLinkFromChat_ShouldThrowException_WhenChatDoesNotExist() {
        when(chatDao.existsChat(chatId)).thenReturn(false);

        assertThatThrownBy(() -> chatService.deleteLinkFromChat(chatId, linkUrl))
                .isInstanceOf(ChatNotFoundException.class);
    }

    @Test
    void deleteLinkFromChat_ShouldThrowException_WhenLinkDoesNotExist() {
        when(chatDao.existsChat(chatId)).thenReturn(true);
        when(linkDao.getLinkIdByUrl(linkUrl)).thenReturn(null);

        assertThatThrownBy(() -> chatService.deleteLinkFromChat(chatId, linkUrl))
                .isInstanceOf(LinkNotFoundException.class);
    }

    @Test
    void deleteLinkFromChat_ShouldThrowException_WhenLinkNotInChat() {
        when(chatDao.existsChat(chatId)).thenReturn(true);
        when(linkDao.getLinkIdByUrl(linkUrl)).thenReturn(linkId);
        when(linkDao.removeLinkFromChatById(chatId, linkId)).thenReturn(false);

        assertThatThrownBy(() -> chatService.deleteLinkFromChat(chatId, linkUrl))
                .isInstanceOf(LinkNotFoundException.class)
                .hasMessageContaining(chatId.toString(), linkUrl);
    }

    @Test
    void getAllLinks() {
        List<Link> expectedLinks = List.of(link);
        when(linkDao.getAllLinks()).thenReturn(expectedLinks);

        List<Link> links = chatService.getAllLinks();

        assertThat(expectedLinks).isEqualTo(links);
    }

    @Test
    void updateLinkLastModified_ShouldThrowException_WhenLinkDoesNotExist() {
        when(linkDao.getLinkUrlById(linkId)).thenReturn(null);

        assertThatThrownBy(() -> chatService.updateLinkLastModifiedAt(linkId, null))
                .isInstanceOf(LinkNotFoundException.class);
    }
}
