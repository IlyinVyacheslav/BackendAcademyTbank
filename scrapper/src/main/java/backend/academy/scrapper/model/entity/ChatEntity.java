package backend.academy.scrapper.model.entity;

import backend.academy.scrapper.service.digest.NotificationMode;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_mode", nullable = false)
    NotificationMode notificationMode = NotificationMode.IMMEDIATE;

    @ManyToMany(mappedBy = "chats")
    List<LinkEntity> links;
}
