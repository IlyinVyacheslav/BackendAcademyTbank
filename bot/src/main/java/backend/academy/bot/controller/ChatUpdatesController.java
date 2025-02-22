package backend.academy.bot.controller;

import backend.academy.bot.service.BotService;
import backend.academy.dto.Update;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> getUpdates(@Valid @RequestBody Update update) {
        String message = String.format("\"%s\" url was updated: %s", update.url(), update.description());
        update.tgChatIds().forEach(id -> botService.sendMessage(String.valueOf(id), message, false));
        return ResponseEntity.ok("Обновление обработано");
    }
}
