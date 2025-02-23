package backend.academy.scrapper.controller;

import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.ListLinksResponse;
import backend.academy.scrapper.service.ChatService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ScrapperGeneralChatController {
    private final ChatService chatService;

    @Autowired
    public ScrapperGeneralChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/tg-chat/{id}")
    public ResponseEntity<String> registerChat(@PathVariable("id") Long id) {
        chatService.registerChat(id);
        return ResponseEntity.ok("Чат зарегистрирован");
    }

    @DeleteMapping("/tg-chat/{id}")
    public ResponseEntity<String> deleteChat(@PathVariable("id") Long id) {
        chatService.deleteChat(id);
        return ResponseEntity.ok("Чат успешно удалён");
    }

    @GetMapping("/links")
    public ResponseEntity<ListLinksResponse> getAllLinksFromChat(@RequestHeader("Tg-Chat-Id") Long id) {
        return ResponseEntity.ok(chatService.getAllLinksFromChat(id));
    }

    @PostMapping("/links")
    public ResponseEntity<String> addLinkToChat(
            @RequestHeader("Tg-Chat-Id") Long id, @Valid @RequestBody AddLinkRequest linkRequest) {
        log.info("Link with url:{}", linkRequest.link());
        chatService.addLinkToChat(id, linkRequest);
        return ResponseEntity.ok("Ссылка успешно добавлена");
    }

    @DeleteMapping("/links")
    public ResponseEntity<String> deleteLinkFromChat(@RequestHeader("Tg-Chat-Id") Long id, @RequestBody String link) {
        chatService.deleteLinkFromChat(id, link);
        return ResponseEntity.ok("Ссылка успешно удалена");
    }
}
