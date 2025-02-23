package backend.academy.scrapper.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.ListLinksResponse;
import backend.academy.scrapper.controller.ScrapperGeneralChatController;
import backend.academy.scrapper.service.ChatService;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class ScrapperGeneralChatControllerTest {

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ScrapperGeneralChatController scrapperGeneralChatController;

    @Test
    public void testRegisterChat() {
        Long chatId = 1L;
        doNothing().when(chatService).registerChat(chatId);

        ResponseEntity<String> response = scrapperGeneralChatController.registerChat(chatId);

        assertThat("Чат зарегистрирован").isEqualTo(response.getBody());
        assertThat(200).isEqualTo(response.getStatusCode().value());
        verify(chatService).registerChat(chatId);
    }

    @Test
    public void testDeleteChat() {
        Long chatId = 1L;
        doNothing().when(chatService).deleteChat(chatId);

        ResponseEntity<String> response = scrapperGeneralChatController.deleteChat(chatId);

        assertThat("Чат успешно удалён").isEqualTo(response.getBody());
        assertThat(200).isEqualTo(response.getStatusCode().value());
        verify(chatService).deleteChat(chatId);
    }

    @Test
    public void testGetAllLinksFromChat() {
        Long chatId = 1L;
        ListLinksResponse mockedResponse = new ListLinksResponse(Collections.emptyList(), 0);
        when(chatService.getAllLinksFromChat(chatId)).thenReturn(mockedResponse);

        ResponseEntity<ListLinksResponse> response = scrapperGeneralChatController.getAllLinksFromChat(chatId);

        assertThat(mockedResponse).isEqualTo(response.getBody());
        assertThat(200).isEqualTo(response.getStatusCode().value());
        verify(chatService).getAllLinksFromChat(chatId);
    }

    @Test
    public void testAddLinkToChat() {
        Long chatId = 1L;
        AddLinkRequest linkRequest = new AddLinkRequest("link.com", null, null);
        doNothing().when(chatService).addLinkToChat(chatId, linkRequest);

        ResponseEntity<String> response = scrapperGeneralChatController.addLinkToChat(chatId, linkRequest);

        assertThat("Ссылка успешно добавлена").isEqualTo(response.getBody());
        assertThat(200).isEqualTo(response.getStatusCode().value());
        verify(chatService).addLinkToChat(chatId, linkRequest);
    }

    @Test
    public void testDeleteLinkFromChat() {
        Long chatId = 1L;
        String url = "link.com";
        doNothing().when(chatService).deleteLinkFromChat(chatId, url);

        ResponseEntity<String> response = scrapperGeneralChatController.deleteLinkFromChat(chatId, url);

        assertThat("Ссылка успешно удалена").isEqualTo(response.getBody());
        assertThat(200).isEqualTo(response.getStatusCode().value());
        verify(chatService).deleteLinkFromChat(chatId, url);
    }
}
