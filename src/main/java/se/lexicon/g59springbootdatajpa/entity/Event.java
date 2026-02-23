package se.lexicon.g59springbootdatajpa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "createdBy")
@EqualsAndHashCode(exclude = "createdBy")

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@UuidGenerator(style = UuidGenerator.Style.TIME)
    @Setter
    private Long id;

    @Column(nullable = false)
    @Setter
    private String title;

    @Column(nullable = false)
    @Setter
    private LocalDateTime dateTime;

    @Setter
    private String location;

    @Setter
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "event_participants",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants = new HashSet<>();

    public void addParticipant(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        participants.add(user);
    }

    public void removeParticipant(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        participants.remove(user);
    }
}
