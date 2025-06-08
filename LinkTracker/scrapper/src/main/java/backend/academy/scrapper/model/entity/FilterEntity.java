package backend.academy.scrapper.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "filters")
@Entity
public class FilterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "filter_id_gen")
    @SequenceGenerator(name = "filter_id_gen", sequenceName = "filter_id_seq", allocationSize = 1)
    private Long filterId;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private ChatEntity chat;

    @ManyToOne
    @JoinColumn(name = "link_id")
    private LinkEntity link;

    @Column(name = "filter", nullable = false)
    private String filter;
}
