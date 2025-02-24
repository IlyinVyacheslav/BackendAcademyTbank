package backend.academy.scrapper.exc;

import backend.academy.dto.ApiErrorResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ApiErrorResponse response = new ApiErrorResponse(
                "Ошибка валидации полей", "400", ex.getClass().getSimpleName(), ex.getMessage(), errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(BaseChatRepositoryException.class)
    public ResponseEntity<ApiErrorResponse> handleChatRepositoryException(BaseChatRepositoryException e) {
        ApiErrorResponse response = new ApiErrorResponse(
                "Ошибка в репозитории",
                Integer.toString(HttpStatus.NOT_FOUND.value()),
                e.getClass().getSimpleName(),
                e.getMessage(),
                List.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
