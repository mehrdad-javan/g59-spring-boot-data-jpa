package se.lexicon.g59springbootdatajpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 100, nullable = false)
    private String email;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, updatable = false)
    private Instant createDate;


    @PrePersist
    private void prePersist(){
        createDate = Instant.now();
    }


}
