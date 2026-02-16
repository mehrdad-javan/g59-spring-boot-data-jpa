package se.lexicon.g59springbootdatajpa.entity;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder

@Entity
//@Table(name = "tbl_user_profile")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String nickname;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 500)
    private String bio;

    @Column(length = 255)
    private String address;

    @Lob
    @Column(length = 1000000) // 1 MB
    private byte[] profileImage;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;


}
