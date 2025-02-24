package backend.academy.scrapper.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.ListLinksResponse;
import backend.academy.scrapper.controller.ScrapperGeneralChatController;
import backend.academy.scrapper.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ScrapperGeneralChatController.class)
public class ScrapperGeneralChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChatService chatService;

    @Test
    public void testRegisterChat() throws Exception {
        Long chatId = 1L;
        doNothing().when(chatService).registerChat(chatId);

        mockMvc.perform(post("/tg-chat/{chatId}", chatId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Чат зарегистрирован")));
        verify(chatService).registerChat(chatId);
    }

    @Test
    public void testDeleteChat() throws Exception {
        Long chatId = 1L;
        doNothing().when(chatService).deleteChat(chatId);

        mockMvc.perform(delete("/tg-chat/{chatId}", chatId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Чат успешно удалён")));
        verify(chatService).deleteChat(chatId);
    }

    @Test
    public void testGetAllLinksFromChat() throws Exception {
        Long chatId = 1L;
        ListLinksResponse mockedResponse = new ListLinksResponse(Collections.emptyList(), 0);
        when(chatService.getAllLinksFromChat(chatId)).thenReturn(mockedResponse);

        mockMvc.perform(get("/links").header("Tg-Chat-Id", chatId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.links").isArray())
                .andExpect(jsonPath("$.links.length()").value(0))
                .andExpect(jsonPath("$.size").value(0));

        verify(chatService).getAllLinksFromChat(chatId);
    }

    @Test
    public void testAddLinkToChat() throws Exception {
        Long chatId = 1L;
        AddLinkRequest linkRequest = new AddLinkRequest("link.com", null, null);
        doNothing().when(chatService).addLinkToChat(chatId, linkRequest);
        ArgumentCaptor<AddLinkRequest> captor = ArgumentCaptor.forClass(AddLinkRequest.class);

        mockMvc.perform(post("/links")
                        .header("Tg-Chat-Id", chatId.toString())
                        .content(objectMapper.writeValueAsString(linkRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(comparesEqualTo("Ссылка успешно добавлена")));

        verify(chatService).addLinkToChat(eq(chatId), captor.capture());
        AddLinkRequest actualLinkRequest = captor.getValue();
        assertThat(actualLinkRequest.link()).isEqualTo(linkRequest.link());
        assertThat(actualLinkRequest.tags()).isEqualTo(linkRequest.tags());
        assertThat(actualLinkRequest.filters()).isEqualTo(linkRequest.filters());
    }

    @Test
    public void testDeleteLinkFromChat() throws Exception {
        Long chatId = 1L;
        String url = "link.com";
        doNothing().when(chatService).deleteLinkFromChat(chatId, url);

        mockMvc.perform(delete("/links")
                        .header("Tg-Chat-Id", chatId.toString())
                        .content(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(comparesEqualTo("Ссылка успешно удалена")));

        verify(chatService).deleteLinkFromChat(chatId, url);
    }
}
