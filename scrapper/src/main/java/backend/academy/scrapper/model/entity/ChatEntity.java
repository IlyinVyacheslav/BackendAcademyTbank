package backend.academy.scrapper.model.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Table(name = "chats")
@Entity
public class ChatEntity {
    @Id
    private Long chatId;

    @ManyToMany(mappedBy = "chats")
    List<LinkEntity> links;
}
