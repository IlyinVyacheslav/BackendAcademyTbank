package backend.academy.bot.service;

import backend.academy.bot.clients.ScrapperClient;
import backend.academy.dto.AddLinkRequest;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BotService implements BotMessages {
    private final TelegramBot bot;
    private final ScrapperClient scrapperClient;
    private final Map<String, AddLinkRequest> userLinks = new ConcurrentHashMap<>();

    @Autowired
    public BotService(TelegramBot telegramBot, ScrapperClient scrapperClient) {
        this.bot = telegramBot;
        this.scrapperClient = scrapperClient;
    }

    @PostConstruct
    public void init() {
        bot.setUpdatesListener(updates -> {
            handleUpdates(updates);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
        log.info("✅ Telegram Bot запущен!");
    }

    private void handleUpdates(List<Update> updates) {
        for (Update update : updates) {
            Message message = update.message();
            log.info("Received message: {}", message);
            if (message != null && message.text() != null) {
                String chatId = message.chat().id().toString();
                String receivedText = message.text();
                Message replyTo = message.replyToMessage();

                if (replyTo != null) {
                    processReply(chatId, receivedText, replyTo);
                } else if (receivedText.startsWith("/")) {
                    handleCommand(chatId, receivedText);
                } else {
                    handleUnknownCommand(chatId);
                }
                log.info("📩 Новое сообщение от {}: {}", chatId, receivedText);
            }
        }
    }

    private void processReply(String chatId, String receivedText, Message replyTo) {
        String replyText = replyTo.text();

        if (replyText != null) {
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
                    link.filters().addAll(List.of(receivedText.split(" ")));
                    scrapperClient
                            .addLink(Long.parseLong(chatId), link)
                            .subscribe(response -> sendMessage(chatId, response));
                    userLinks.remove(chatId);
                }
            } else if (replyText.contains(UNTRACK_LINK_MESSAGE)) {
                scrapperClient
                        .removeLink(Long.parseLong(chatId), receivedText)
                        .subscribe(response -> sendMessage(chatId, response));
            }
        }
    }

    private void handleCommand(String chatId, String commandText) {
        Command command;
        try {
            command = Command.getCommand(commandText);
        } catch (Exception e) {
            log.error("Unknown command", e);
            handleUnknownCommand(chatId);
            return;
        }
        Long chatIdToLong;
        try {
            chatIdToLong = Long.parseLong(chatId);
        } catch (NumberFormatException e) {
            log.error("Incorrect chat id", e);
            return;
        }

        switch (command) {
            case START -> scrapperClient
                    .registerChat(chatIdToLong)
                    .subscribe(response -> sendMessage(chatId, response));
            case HELP -> sendMessage(chatId, COMMANDS_LIST);
            case LIST -> scrapperClient
                    .getAllLinks(chatIdToLong)
                    .subscribe(links -> sendMessage(chatId, links.toString()));
            case TRACK -> sendMessage(chatId, SEND_LINK_MESSAGE, true);
            case UNTRACK -> sendMessage(chatId, UNTRACK_LINK_MESSAGE, true);
        }
    }

    private void handleUnknownCommand(String chatId) {
        sendMessage(chatId, UNKNOWN_COMMAND);
    }

    public void sendMessage(String chatId, String text) {
        sendMessage(chatId, text, false);
    }

    public void sendMessage(String chatId, String text, boolean forceReply) {
        SendMessage message = new SendMessage(chatId, text);
        if (forceReply) {
            message.replyMarkup(new ForceReply());
        }
        bot.execute(message);
    }
}
