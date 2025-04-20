package backend.academy.bot.controller;

import backend.academy.bot.service.BotService;
import backend.academy.dto.LinkUpdate;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatUpdatesController extends BaseChatUpdatesController {
    @Autowired
    public ChatUpdatesController(BotService botService) {
        super(botService);
    }

    @PostMapping("/updates")
    public String postUpdates(@Valid @RequestBody LinkUpdate linkUpdate) {
        processLinkUpdate(linkUpdate);
        return "Обновление обработано";
    }
}
