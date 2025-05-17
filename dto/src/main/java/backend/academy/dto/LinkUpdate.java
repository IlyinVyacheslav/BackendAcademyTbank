package backend.academy.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LinkUpdate {
    private Long id;

    @NotBlank(message = "Url cannot be blank")
    private String url;

    @NotNull
    private String description;

    @NotNull
    private List<Long> tgChatIds;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkUpdate that = (LinkUpdate) o;
        return Objects.equals(id, that.id)
                && Objects.equals(url, that.url)
                && Objects.equals(description, that.description)
                && Objects.equals(tgChatIds, that.tgChatIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, description, tgChatIds);
    }
}
