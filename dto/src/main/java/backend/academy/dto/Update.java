package backend.academy.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class Update {
    private Long id;

    @NotBlank(message = "Url cannot be blank")
    private String url;

    @NotNull
    private String description;

    @NotNull
    private List<Long> tgChatIds;
}
