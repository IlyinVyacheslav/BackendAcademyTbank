package backend.academy.bot.service;

import backend.academy.bot.clients.ScrapperClient;
import backend.academy.bot.exceptions.IllegalCommandException;
import backend.academy.bot.exceptions.InvalidChatIdException;
import backend.academy.bot.service.commands.Command;
import backend.academy.dto.AddLinkRequest;
import backend.academy.logger.LoggerHelper;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BotService implements BotMessages {
    private final TelegramBot bot;
    private final ScrapperClient scrapperClient;
    private final Map<String, AddLinkRequest> userLinks = new ConcurrentHashMap<>();
    private final Map<String, Command> commandsMap;

    @Autowired
    public BotService(List<Command> commands, TelegramBot telegramBot, ScrapperClient scrapperClient) {
        this.bot = telegramBot;
        this.scrapperClient = scrapperClient;
        commands.forEach(x -> LoggerHelper.info(x.getCommand()));
        commandsMap = commands.stream().collect(Collectors.toMap(Command::getCommand, Function.identity()));
    }

    private static long getChatIdToLong(String chatId) {
        try {
            return Long.parseLong(chatId);
        } catch (NumberFormatException e) {
            throw new InvalidChatIdException(chatId);
        }
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onEventWithout() {
        bot.setUpdatesListener(updates -> {
            handleUpdates(updates);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
        setMyCommands();
        LoggerHelper.info("✅ Telegram Bot запущен!");
    }

    private void setMyCommands() {
        BotCommand[] commands = commandsMap.values().stream()
                .map(cmd -> new BotCommand(cmd.getCommand(), cmd.getDescription()))
                .toArray(BotCommand[]::new);
        bot.execute(new SetMyCommands(commands));
    }

    private void handleUpdates(List<Update> updates) {
        for (Update update : updates) {
            Message message = update.message();
            if (message != null && update.message() != null) {
                LoggerHelper.info("Received message", Map.of("message", message));
            } else {
                return;
            }
            if (message.text() != null) {
                String chatId = message.chat().id().toString();
                String receivedText = message.text();
                Message replyTo = message.replyToMessage();

                try {
                    if (replyTo != null) {
                        processReply(chatId, receivedText, replyTo);
                    } else if (receivedText.startsWith("/")) {
                        handleCommand(chatId, receivedText);
                    } else {
                        handleUnknownCommand(chatId);
                    }
                    LoggerHelper.info(
                            "📩 Новое сообщение из чата", Map.of("chatId", chatId, "receivedText", receivedText));
                } catch (IllegalCommandException e) {
                    LoggerHelper.warn(
                            "❌ Неизвестная команда из чата", Map.of("chatId", chatId, "receiverText", receivedText));
                    handleUnknownCommand(chatId);
                } catch (InvalidChatIdException e) {
                    LoggerHelper.error("❌ Чат с неправильным ID", Map.of("chatId", chatId), e);
                }
            }
        }
    }

    private void processReply(String chatId, String receivedText, Message replyTo) {
        String replyText = replyTo.text();

        if (replyText == null) {
            return;
        }

        if (replyText.contains(SEND_LINK_MESSAGE)) {
            userLinks.put(chatId, new AddLinkRequest(receivedText, new ArrayList<>(), new ArrayList<>()));
            sendMessage(chatId, SEND_TAGS_MESSAGE, true);
        } else if (replyText.contains(SEND_TAGS_MESSAGE)) {
            AddLinkRequest link = userLinks.get(chatId);
            if (link != null) {
                link.tags().addAll(List.of(receivedText.split("\\s")));
                sendMessage(chatId, SEND_FILTERS_MESSAGE, true);
            }
        } else if (replyText.contains(SEND_FILTERS_MESSAGE)) {
            AddLinkRequest link = userLinks.get(chatId);
            if (link != null) {
                link.filters().addAll(List.of(receivedText.split("\\s")));
                scrapperClient
                        .addLink(getChatIdToLong(chatId), link)
                        .subscribe(response -> sendMessage(chatId, response));
                userLinks.remove(chatId);
            }
        } else if (replyText.contains(UNTRACK_LINK_MESSAGE)) {
            scrapperClient
                    .removeLink(getChatIdToLong(chatId), receivedText)
                    .subscribe(response -> sendMessage(chatId, response));
        }
    }

    public void handleCommand(String chatId, String commandText) {
        Command command = commandsMap.get(commandText);
        if (command == null) {
            handleUnknownCommand(chatId);
            return;
        }
        Long chatIdToLong = getChatIdToLong(chatId);
        String response = command.execute(scrapperClient, chatIdToLong);
        sendMessage(chatId, response, command.shouldBeReplied());
    }

    public void handleUnknownCommand(String chatId) {
        sendMessage(chatId, UNKNOWN_COMMAND);
    }

    public void sendMessage(String chatId, String text) {
        sendMessage(chatId, text, false);
    }

    private void sendMessage(String chatId, String text, boolean forceReply) {
        SendMessage message = new SendMessage(chatId, text);
        if (forceReply) {
            message.replyMarkup(new ForceReply());
        }
        bot.execute(message);
    }
}
