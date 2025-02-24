package backend.academy.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddLinkRequest {
    @NotBlank(message = "Url cannot be blank")
    private String link;

    private List<String> tags;
    private List<String> filters;
}
