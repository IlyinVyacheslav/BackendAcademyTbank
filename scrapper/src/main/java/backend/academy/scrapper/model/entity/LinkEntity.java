package backend.academy.scrapper.model.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Table(name = "links")
@Entity
public class LinkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "link_id_seq")
    @SequenceGenerator(name = "link_id_seq", sequenceName = "link_id_seq", allocationSize = 1)
    private Long linkId;

    @ManyToMany
    @JoinTable(
            name = "chat_links",
            joinColumns = @JoinColumn(name = "link_id"),
            inverseJoinColumns = @JoinColumn(name = "chat_id"))
    private List<ChatEntity> chats;

    @Column(name = "url", nullable = false)
    private String url;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified")
    private Timestamp lastModified;
}
