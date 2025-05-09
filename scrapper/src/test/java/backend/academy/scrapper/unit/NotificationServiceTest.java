package backend.academy.scrapper.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.dto.LinkUpdate;
import backend.academy.scrapper.clients.Notifications;
import backend.academy.scrapper.clients.bot.BotClientHttp;
import backend.academy.scrapper.clients.web.GitHubClient;
import backend.academy.scrapper.clients.web.StackOverflowClient;
import backend.academy.scrapper.exc.LinkNotFoundException;
import backend.academy.scrapper.model.dto.Link;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.service.NotificationService;
import backend.academy.scrapper.service.digest.DigestStorage;
import backend.academy.scrapper.service.digest.NotificationMode;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private ChatService chatService;

    @Mock
    private GitHubClient gitHubClient;

    @Mock
    private StackOverflowClient stackOverflowClient;

    @Mock
    private BotClientHttp botClientHttp;

    @Mock
    private DigestStorage digestStorage;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void checkNotifications_ShouldNotFinishWithError_WhenErrorOccursInRepository() {
        Link nonExistingtLink = new Link(3L, "github.com/test/repo", null);
        Timestamp time = Timestamp.valueOf("2024-02-22 10:00:00");
        when(chatService.getAllLinksAsBatchStream()).thenReturn(Stream.of(List.of(nonExistingtLink)));
        when(gitHubClient.getNewNotifications(nonExistingtLink.url(), nonExistingtLink.lastModified()))
                .thenReturn(Mono.just(new Notifications("New commit", time)));
        doThrow(LinkNotFoundException.class).when(chatService).updateLinkLastModifiedAt(nonExistingtLink.id(), time);

        assertThatNoException().isThrownBy(() -> notificationService.checkNotifications());

        verify(botClientHttp, never()).postUpdates(any(LinkUpdate.class));
        assertThatThrownBy(() -> chatService.updateLinkLastModifiedAt(
                        nonExistingtLink.id(), Timestamp.valueOf("2024-02-22 10:00:00")))
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
        Link previousLink = new Link(4L, "github.com/user1/repo1", Timestamp.valueOf("2024-02-22 10:00:00"));
        when(chatService.getAllLinksAsBatchStream()).thenReturn(Stream.of(List.of(previousLink)));
        when(gitHubClient.getNewNotifications(previousLink.url(), previousLink.lastModified()))
                .thenReturn(Mono.just(new Notifications("New commit", previousLink.lastModified())));

        notificationService.checkNotifications();

        verify(chatService, never()).updateLinkLastModifiedAt(eq(previousLink.id()), any(Timestamp.class));
        verify(botClientHttp, never()).postUpdates(any(LinkUpdate.class));
    }

    @Test
    void checkNotifications_ShouldNotSendUpdate_WhenUserIsInFilters() {
        Link link = new Link(1L, "github.com/user1/repo1", Timestamp.valueOf("2024-02-22 10:00:00"));
        when(chatService.getAllLinksAsBatchStream()).thenReturn(Stream.of(List.of(link)));
        when(gitHubClient.getNewNotifications(link.url(), link.lastModified()))
                .thenReturn(Mono.just(new Notifications(
                        "New commit", Timestamp.valueOf("2024-03-22 10:00:00"), Optional.of("slava_itmo"))));
        when(chatService.getAllChatIdsByLinkId(1L)).thenReturn(List.of(1L));
        when(chatService.getFiltersByChatIdAndLinkId(1L, 1L)).thenReturn(List.of("slava_itmo", "vadim_itmo"));

        notificationService.checkNotifications();

        verify(botClientHttp, never()).postUpdates(any(LinkUpdate.class));
    }

    @Test
    void checkNotifications_ShouldSendUpdate_WhenUserIsNotInFilters() {
        Link link = new Link(1L, "github.com/user1/repo1", Timestamp.valueOf("2024-02-22 10:00:00"));
        when(chatService.getAllLinksAsBatchStream()).thenReturn(Stream.of(List.of(link)));
        when(gitHubClient.getNewNotifications(link.url(), link.lastModified()))
                .thenReturn(Mono.just(new Notifications(
                        "New commit", Timestamp.valueOf("2024-04-22 10:00:00"), Optional.of("slava_itmo"))));
        when(chatService.getAllChatIdsByLinkId(1L)).thenReturn(List.of(1L));
        when(chatService.getFiltersByChatIdAndLinkId(1L, 1L)).thenReturn(List.of("tyoma_itmo", "vadim_itmo"));
        when(botClientHttp.postUpdates(any())).thenReturn(Mono.just("ok"));

        notificationService.checkNotifications();

        StepVerifier.create(botClientHttp.postUpdates(new LinkUpdate(link.id(), link.url(), "New commit", List.of(1L))))
                .expectNext("ok")
                .verifyComplete();
    }

    @Test
    void checkNotification_shouldStoreUpdatesInDigestStorage_WhenNotificationModeDigest() {
        Link link = new Link(1L, "github.com/user3/repo3", Timestamp.valueOf("2024-02-22 10:00:00"));
        when(chatService.getAllLinksAsBatchStream()).thenReturn(Stream.of(List.of(link)));
        when(gitHubClient.getNewNotifications(link.url(), link.lastModified()))
                .thenReturn(Mono.just(new Notifications(
                        "New commit", Timestamp.valueOf("2024-02-22 12:00:00"), Optional.of("slava_itmo"))));
        when(chatService.getAllChatIdsByLinkId(1L)).thenReturn(List.of(1L));
        when(chatService.getNotificationMode(1L)).thenReturn(NotificationMode.DIGEST);
        ArgumentCaptor<LinkUpdate> captor = ArgumentCaptor.forClass(LinkUpdate.class);

        notificationService.checkNotifications();

        verify(digestStorage, times(1)).addToDigest(captor.capture());
        LinkUpdate actualLinkUpdate = captor.getValue();
        assertThat(actualLinkUpdate.url()).isEqualTo(link.url());
        assertThat(actualLinkUpdate.id()).isEqualTo(link.id());
        assertThat(actualLinkUpdate.tgChatIds()).containsExactlyInAnyOrderElementsOf(List.of(1L));
        assertThat(actualLinkUpdate.description()).isEqualTo("New commit");

        verify(botClientHttp, never()).postUpdates(any(LinkUpdate.class));
    }

    private void verifyNotificationsSentToSubscribedChats(Link link, List<Long> expectedChatIds) {
        when(chatService.getAllLinksAsBatchStream()).thenReturn(Stream.of(List.of(link)));
        when(gitHubClient.getNewNotifications(link.url(), link.lastModified()))
                .thenReturn(Mono.just(new Notifications("New commit", Timestamp.valueOf("2024-02-22 10:00:00"))));
        when(botClientHttp.postUpdates(any())).thenReturn(Mono.just("ok"));

        notificationService.checkNotifications();

        verify(chatService, times(1)).updateLinkLastModifiedAt(link.id(), Timestamp.valueOf("2024-02-22 10:00:00"));
        StepVerifier.create(
                        botClientHttp.postUpdates(new LinkUpdate(link.id(), link.url(), "New commit", expectedChatIds)))
                .expectNext("ok")
                .verifyComplete();
    }
}
