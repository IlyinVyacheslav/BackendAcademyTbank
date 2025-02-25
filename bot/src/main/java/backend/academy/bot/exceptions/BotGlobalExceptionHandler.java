package backend.academy.bot.exceptions;

import backend.academy.dto.ApiErrorResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BotGlobalExceptionHandler {
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
    //    public ResponseEntity<Map<String, String>> handleIllegalCommandException(IllegalCommandException ex) {
    //        return
    //    }
}
