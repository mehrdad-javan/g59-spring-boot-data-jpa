package se.lexicon.g59springbootdatajpa.reposiroty;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import se.lexicon.g59springbootdatajpa.entity.User;
import se.lexicon.g59springbootdatajpa.repository.UserRepository;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository testObject;

    @Test
    @DisplayName("save() should persist user and set createDate via @PrePersist")
    void save_shouldPersistUser_andSetCreateDate(){
        // Arrange
        User user = new User();
        user.setFullName("Alice Andersson");
        user.setEmail("alice@example.com");

        // Act
        User savedUser = testObject.save(user);

        // Assert
        assertNotNull(savedUser.getId());
        assertNotNull(savedUser.getCreateDate());
    }
    // add more tests using AI if needed


}
