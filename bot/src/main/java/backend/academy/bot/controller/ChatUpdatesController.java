package backend.academy.bot.controller;

import backend.academy.bot.service.BotService;
import backend.academy.dto.LinkUpdate;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatUpdatesController {
    private final BotService botService;

    @Autowired
    public ChatUpdatesController(BotService botService) {
        this.botService = botService;
    }

    @PostMapping("/updates")
    public String postUpdates(@Valid @RequestBody LinkUpdate linkUpdate) {
        String message = String.format("\"%s\" url was updated: %s", linkUpdate.url(), linkUpdate.description());
        linkUpdate.tgChatIds().forEach(id -> botService.sendMessage(String.valueOf(id), message));
        return "Обновление обработано";
    }
}
