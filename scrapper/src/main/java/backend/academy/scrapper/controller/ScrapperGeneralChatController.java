package backend.academy.scrapper.controller;

import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.ListLinksResponse;
import backend.academy.dto.ListLinksUpdate;
import backend.academy.logger.LoggerHelper;
import backend.academy.scrapper.service.ChatService;
import jakarta.validation.Valid;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/links/tagged")
    public ResponseEntity<List<String>> getLinksByTagFromChat(
            @RequestHeader("Tg-Chat-Id") Long id, @RequestParam("tag") String tag) {
        return ResponseEntity.ok(chatService.getLinksUrlsByTagFromChat(id, tag));
    }

    @GetMapping("/links/updates")
    public ResponseEntity<ListLinksUpdate> getLinksUpdatesFromChatByTagAndTime(
            @RequestHeader("Tg-Chat-Id") Long id, @RequestParam("tag") String tag, @RequestBody Long fromTimestamp) {
        Timestamp from = new Timestamp(fromTimestamp);
        return ResponseEntity.ok(chatService.getLinksUrlsByTagAndTimeFromChat(id, tag, from));
    }

    @PostMapping("/links")
    public ResponseEntity<String> addLinkToChat(
            @RequestHeader("Tg-Chat-Id") Long id, @Valid @RequestBody AddLinkRequest linkRequest) {
        LoggerHelper.info("Link with url", Map.of("link", linkRequest.link()));
        chatService.addLinkToChat(id, linkRequest);
        return ResponseEntity.ok("Ссылка успешно добавлена");
    }

    @DeleteMapping("/links")
    public ResponseEntity<String> deleteLinkFromChat(@RequestHeader("Tg-Chat-Id") Long id, @RequestBody String link) {
        chatService.deleteLinkFromChat(id, link);
        return ResponseEntity.ok("Ссылка успешно удалена");
    }
}
