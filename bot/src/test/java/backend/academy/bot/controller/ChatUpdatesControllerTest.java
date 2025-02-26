package backend.academy.bot.controller;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.bot.service.BotService;
import backend.academy.dto.LinkUpdate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChatUpdatesController.class)
class ChatUpdatesControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BotService botService;

    @Test
    void testPostUpdates() throws Exception {
        doNothing().when(botService).sendMessage(anyString(), anyString());
        LinkUpdate linkUpdate = new LinkUpdate(1L, "github.com", "Something happened", List.of(1L, 2L));

        mockMvc.perform(post("/updates")
                        .content(objectMapper.writeValueAsString(linkUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(comparesEqualTo("Обновление обработано")));

        verify(botService).sendMessage(eq("1"), anyString());
        verify(botService).sendMessage(eq("2"), anyString());
    }

    @Test
    void testPostIncorrectUpdatesWithEmptyBody() throws Exception {
        mockMvc.perform(post("/updates").content("{}").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Ошибка валидации полей"))
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.exceptionName").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.exceptionMessage").exists())
                .andExpect(jsonPath("$.stackTrace").isArray())
                .andExpect(jsonPath("$.stackTrace.length()").value(3))
                .andExpect(jsonPath("$.stackTrace")
                        .value(containsInAnyOrder(
                                "url: Url cannot be blank",
                                "description: must not be null",
                                "tgChatIds: must not be null")));
    }
}
