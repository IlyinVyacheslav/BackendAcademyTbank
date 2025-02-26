package backend.academy.bot.exceptions;

import backend.academy.bot.service.BotService;
import backend.academy.dto.ApiErrorResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class BotGlobalExceptionHandler {
    private final BotService botService;

    @Autowired
    public BotGlobalExceptionHandler(BotService botService) {
        this.botService = botService;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ApiErrorResponse response = new ApiErrorResponse(
                "Ошибка валидации полей", "400", ex.getClass().getSimpleName(), ex.getMessage(), errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    //    @ExceptionHandler(IllegalCommandException.class)
    //    public void handleIllegalCommandException(IllegalCommandException ex) {
    //        botService.handleUnknownCommand(ex.chatId());
    //    }
    //
    //    @ExceptionHandler(InvalidChatIdException.class)
    //    public void handleInvalidChatIdException(InvalidChatIdException ex) {
    //        log.error("Incorrect chat id", ex);
    //    }
}
