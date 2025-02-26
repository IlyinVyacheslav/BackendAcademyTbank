package backend.academy.bot.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import backend.academy.bot.clients.ScrapperClient;
import backend.academy.bot.exceptions.IllegalCommandException;
import backend.academy.bot.exceptions.InvalidChatIdException;
import com.pengrad.telegrambot.TelegramBot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BotServiceTest {
    @Mock
    private ScrapperClient scrapperClient;

    @Mock
    private TelegramBot telegramBot;

    @InjectMocks
    private BotService botService;

    @Test
    void handleCommand_ShouldThrowIllegalCommandException_WhenCommandIsInvalid() {
        String chatId = "12345";
        String unknownCommand = "/unknown_command";

        assertThatThrownBy(() -> botService.handleCommand(chatId, unknownCommand))
                .isInstanceOf(IllegalCommandException.class);
    }

    @Test
    void handleCommand_ShouldThrowIllegalCommandException_WhenCommandIsNull() {
        assertThatThrownBy(() -> botService.handleCommand(null, "command")).isInstanceOf(IllegalCommandException.class);
    }

    @Test
    void handleCommand_ShouldThrowInvalidChatIdException_WhenChatIdIsInvalid() {
        String chatId = "chat_id";
        String command = "/help";

        assertThatThrownBy(() -> botService.handleCommand(chatId, command)).isInstanceOf(InvalidChatIdException.class);
    }

    @Test
    void handleCommand_ShouldThrowInvalidChatIdException_WhenChatIdIsNull() {
        String command = "/help";
        assertThatThrownBy(() -> botService.handleCommand(null, command)).isInstanceOf(InvalidChatIdException.class);
    }
}
