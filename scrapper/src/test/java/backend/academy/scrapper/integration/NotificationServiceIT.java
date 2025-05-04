package backend.academy.scrapper.integration;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.LinkUpdate;
import backend.academy.scrapper.clients.Notifications;
import backend.academy.scrapper.clients.bot.BotClientHttp;
import backend.academy.scrapper.clients.web.GitHubClient;
import backend.academy.scrapper.exc.LinkNotFoundException;
import backend.academy.scrapper.model.dto.Link;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.service.NotificationService;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@TestPropertySource(properties = "app.message-transport=HTTP")
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
    private BotClientHttp botClientHttp;

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
        when(chatService.getAllLinksAsBatchStream()).thenReturn(Stream.of(List.of(nonExistingtLink)));
        when(gitHubClient.getNewNotifications(nonExistingtLink.url(), nonExistingtLink.lastModified()))
                .thenReturn(Mono.just(new Notifications("New commit", Timestamp.valueOf("2024-02-22 10:00:00"))));

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
        Link link = new Link(1L, "github.com/user1/repo1", null);
        when(chatService.getAllLinksAsBatchStream()).thenReturn(Stream.of(List.of(link)));
        when(gitHubClient.getNewNotifications(link.url(), null))
                .thenReturn(Mono.just(new Notifications("New commit", link.lastModified(), Optional.of("slava_itmo"))));
        when(chatService.getFiltersByChatIdAndLinkId(1L, 1L)).thenReturn(List.of("slava_itmo", "vadim_itmo"));

        notificationService.checkNotifications();

        verify(botClientHttp, never()).postUpdates(any(LinkUpdate.class));
    }

    @Test
    void checkNotifications_ShouldSendUpdate_WhenUserIsNotInFilters() {
        Link link = new Link(1L, "github.com/user1/repo1", Timestamp.valueOf("2024-02-22 10:00:00"));
        when(chatService.getAllLinksAsBatchStream()).thenReturn(Stream.of(List.of(link)));
        when(gitHubClient.getNewNotifications(link.url(), Timestamp.valueOf("2024-02-22 10:00:00")))
                .thenReturn(Mono.just(new Notifications("New commit", link.lastModified(), Optional.of("slava_itmo"))));
        when(chatService.getFiltersByChatIdAndLinkId(1L, 1L)).thenReturn(List.of("tyoma_itmo", "vadim_itmo"));
        when(botClientHttp.postUpdates(any())).thenReturn(Mono.just("ok"));

        notificationService.checkNotifications();

        StepVerifier.create(botClientHttp.postUpdates(new LinkUpdate(link.id(), link.url(), "New commit", List.of(1L))))
                .expectNext("ok")
                .verifyComplete();
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
