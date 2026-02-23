package se.lexicon.g59springbootdatajpa.reposiroty;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import se.lexicon.g59springbootdatajpa.entity.User;
import se.lexicon.g59springbootdatajpa.repository.UserRepository;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("save() should persist user and set createDate via @PrePersist")
    void save_shouldPersistUser_andSetCreateDate() {
        // Arrange
        User user = new User();
        user.setEmail("alice@example.com");
        user.setFullName("Alice Andersson");

        // Act
        User saved = userRepository.save(user);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreateDate()).isNotNull();
    }

    @Test
    @DisplayName("findByEmail() should return user when email exists")
    void findByEmail_shouldReturnUser() {
        // Arrange
        User user = new User();
        user.setEmail("bob@example.com");
        user.setFullName("Bob Berg");
        userRepository.save(user);

        // Act + Assert
        assertThat(userRepository.findByEmail("bob@example.com")).isPresent();
    }

    @Test
    @DisplayName("existsByEmail() should be true for existing email")
    void existsByEmail_shouldBeTrue() {
        // Arrange
        User user = new User();
        user.setEmail("carol@example.com");
        user.setFullName("Carol Carlsson");
        userRepository.save(user);

        // Act + Assert
        assertThat(userRepository.existsByEmail("carol@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("missing@example.com")).isFalse();
    }

    @Test
    @DisplayName("findByCreateDateAfter() should return users created after a given instant")
    void findByCreateDateAfter_shouldFilterByDate() {
        // Arrange
        User user1 = new User();
        user1.setEmail("d1@example.com");
        user1.setFullName("D One");
        userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("d2@example.com");
        user2.setFullName("D Two");
        userRepository.save(user2);

        Instant nowMinus1Second = Instant.now().minusSeconds(1);

        // Act
        List<User> result = userRepository.findByCreateDateAfter(nowMinus1Second);

        // Assert
        assertThat(result).hasSize(2);
    }
}
