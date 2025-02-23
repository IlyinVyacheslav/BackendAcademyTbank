package backend.academy.scrapper.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.Update;
import backend.academy.scrapper.clients.BotClient;
import backend.academy.scrapper.clients.GitHubClient;
import backend.academy.scrapper.clients.GitHubNotifications;
import backend.academy.scrapper.exc.LinkNotFoundException;
import backend.academy.scrapper.model.Link;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.service.NotificationService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import reactor.core.publisher.Mono;

@SpringBootTest
@AutoConfigureWebTestClient
public class NotificationServiceIT {
    @Autowired
    private NotificationService notificationService;

    @MockitoSpyBean
    private ChatService chatService;

    @MockitoBean
    private GitHubClient gitHubClient;

    @MockitoBean
    private BotClient botClient;

    @BeforeEach
    void setUp() {
        chatService.registerChat(1L);
        chatService.addLinkToChat(
                1L, new AddLinkRequest("github.com/user1/repo1", Collections.emptyList(), Collections.emptyList()));
        chatService.addLinkToChat(
                1L, new AddLinkRequest("github.com/user2/repo2", Collections.emptyList(), Collections.emptyList()));
        chatService.registerChat(2L);
        chatService.addLinkToChat(
                2L, new AddLinkRequest("github.com/user2/repo2", Collections.emptyList(), Collections.emptyList()));
    }

    @AfterEach
    void cleanUp() {
        chatService.deleteChat(1L);
        chatService.deleteChat(2L);
    }

    @Test
    void checkNotifications_ShouldNotFinishWithError_WhenErrorOccursInRepository() {
        Link nonExistingtLink = new Link(3L, "github.com/test/repo", null);
        when(chatService.getAllLinks()).thenReturn(List.of(nonExistingtLink));
        when(gitHubClient.getNewNotifications(nonExistingtLink.url(), nonExistingtLink.lastModified()))
                .thenReturn(Mono.just(new GitHubNotifications("New commit", "2024-02-22T10:00:00Z")));

        assertThatNoException().isThrownBy(() -> notificationService.checkNotifications());

        verify(botClient, never()).postUpdates(any(Update.class));
        assertThatThrownBy(() -> chatService.updateLinkLastModifiedAt(nonExistingtLink.id(), "2024-02-22T10:00:00Z"))
                .isInstanceOf(LinkNotFoundException.class);
    }

    @Test
    void checkNotifications_ShouldSendUpdateOnlyToSubscribedChats_WhenOnlyOneChatIsSubscribed() {
        verifyNotificationsSentToSubscribedChats(new Link(1L, "github.com/user1/repo1", null), List.of(1L));
    }

    @Test
    void checkNotifications_ShouldSendUpdateOnlyToSubscribedChats_WhenSeveralChatsAreSubscribed() {
        verifyNotificationsSentToSubscribedChats(new Link(2L, "github.com/user2/repo2", null), List.of(1L, 2L));
    }

    @Test
    void checkNotifications_ShouldNotSendUpdate_WhenLastModifiedNotChanged() {
        Link previousLink = new Link(4L, "github.com/user1/repo1", "2024-02-22T10:00:00Z");
        when(chatService.getAllLinks()).thenReturn(List.of(previousLink));
        when(gitHubClient.getNewNotifications(previousLink.url(), previousLink.lastModified()))
                .thenReturn(Mono.just(new GitHubNotifications("New commit", previousLink.lastModified())));

        notificationService.checkNotifications();

        verify(chatService, never()).updateLinkLastModifiedAt(eq(previousLink.id()), anyString());
        verify(botClient, never()).postUpdates(any(Update.class));
    }

    private void verifyNotificationsSentToSubscribedChats(Link link, List<Long> expectedChatIds) {
        when(chatService.getAllLinks()).thenReturn(List.of(link));
        when(gitHubClient.getNewNotifications(link.url(), link.lastModified()))
                .thenReturn(Mono.just(new GitHubNotifications("New commit", "2024-02-22T10:00:00Z")));
        ArgumentCaptor<Update> captor = ArgumentCaptor.forClass(Update.class);

        notificationService.checkNotifications();

        verify(chatService, times(1)).updateLinkLastModifiedAt(link.id(), "2024-02-22T10:00:00Z");
        verify(botClient, times(1)).postUpdates(captor.capture());
        Update update = captor.getValue();
        assertThat(update.url()).isEqualTo(link.url());
        assertThat(update.tgChatIds()).containsExactlyElementsOf(expectedChatIds);
    }
}
