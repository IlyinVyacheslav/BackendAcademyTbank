package backend.academy.bot.controller;

import backend.academy.bot.service.BotService;
import backend.academy.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseChatUpdatesController {
    protected final BotService botService;

    protected void processLinkUpdate(LinkUpdate linkUpdate) {
        String message = String.format("\"%s\" url was updated: %s", linkUpdate.url(), linkUpdate.description());
        linkUpdate.tgChatIds().forEach(id -> botService.sendMessage(String.valueOf(id), message));
    }
}
