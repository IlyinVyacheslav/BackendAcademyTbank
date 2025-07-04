package backend.academy.bot.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.clients.ScrapperClient;
import backend.academy.bot.exceptions.IllegalCommandException;
import backend.academy.bot.exceptions.InvalidChatIdException;
import backend.academy.bot.service.commands.HelpCommand;
import backend.academy.bot.service.commands.ListCommand;
import backend.academy.bot.service.commands.StartCommand;
import backend.academy.bot.service.commands.TrackCommand;
import backend.academy.bot.service.commands.UntrackCommand;
import backend.academy.bot.service.commands.UrlsByTagAndTimeCommand;
import backend.academy.bot.service.commands.UrlsByTagCommand;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = {
            HelpCommand.class,
            ListCommand.class,
            StartCommand.class,
            TrackCommand.class,
            UntrackCommand.class,
            UrlsByTagAndTimeCommand.class,
            UrlsByTagCommand.class,
            BotService.class,
        })
class BotServiceTest {
    private final String chatId = "12345";

    @MockitoBean
    private ScrapperClient scrapperClient;

    @MockitoBean
    private TelegramBot telegramBot;

    @MockitoBean
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private BotService botService;

    @Test
    void handleCommand_ShouldThrowIllegalCommandException_WhenCommandIsInvalid() {
        String unknownCommand = "/unknown_command";

        assertThatThrownBy(() -> botService.handleCommand(chatId, unknownCommand))
                .isInstanceOf(IllegalCommandException.class);
    }

    @Test
    void handleCommand_ShouldThrowIllegalCommandException_WhenCommandIsNull() {
        assertThatThrownBy(() -> botService.handleCommand(chatId, null)).isInstanceOf(IllegalCommandException.class);
        verify(telegramBot, never()).execute(any(SendMessage.class));
    }

    @Test
    void handleCommand_ShouldThrowInvalidChatIdException_WhenChatIdIsInvalid() {
        String chatId = "chat_id";
        String command = "/help";

        assertThatThrownBy(() -> botService.handleCommand(chatId, command)).isInstanceOf(InvalidChatIdException.class);
        verify(telegramBot, never()).execute(any(SendMessage.class));
    }

    @Test
    void handleCommand_ShouldThrowInvalidChatIdException_WhenChatIdIsNull() {
        String command = "/help";
        assertThatThrownBy(() -> botService.handleCommand(null, command)).isInstanceOf(InvalidChatIdException.class);
    }

    @Test
    void handleCommand_ShouldCallBotExecute_WhenCommandIsHelp() {
        String command = "/help";

        botService.handleCommand(chatId, command);

        verify(telegramBot).execute(any(SendMessage.class));
    }

    @Test
    void handleCommand_ShouldSendEmptyListMessage_WhenListIsEmpty() {
        String command = "/list";
        ValueOperations<String, String> valueOperationsMock = Mockito.mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperationsMock);
        when(valueOperationsMock.get(anyString())).thenReturn(null);
        when(scrapperClient.getAllLinks(anyLong())).thenReturn(Mono.just(List.of()));
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);

        botService.handleCommand(chatId, command);

        verify(telegramBot).execute(captor.capture());
        SendMessage message = captor.getValue();
        assertThat(message.getParameters().get("text")).isEqualTo("Вы пока ничего не отслеживаете");
    }
}
