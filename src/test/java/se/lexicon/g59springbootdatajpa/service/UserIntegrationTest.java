package se.lexicon.g59springbootdatajpa.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import se.lexicon.g59springbootdatajpa.dto.request.UserRequestDTO;
import se.lexicon.g59springbootdatajpa.dto.response.UserResponseDTO;
import se.lexicon.g59springbootdatajpa.exception.DataNotFoundException;
import se.lexicon.g59springbootdatajpa.exception.DuplicateEntryException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserIntegrationTest {

    @Autowired
    private UserService userService;

    private UserRequestDTO createDefaultRequest(String email) {
        return new UserRequestDTO(email, "Test User");
    }

    @Test
    @DisplayName("register() should save user and return response")
    void register_shouldSaveUser() {
        // Arrange
        String email = "test@example.com";
        UserRequestDTO request = createDefaultRequest(email);

        // Act
        UserResponseDTO response = userService.register(request);

        // Assert
        assertThat(response.id()).isNotNull();
        assertThat(response.email()).isEqualTo(email);
        assertThat(response.fullName()).isEqualTo("Test User");
        assertThat(response.createDate()).isNotNull();
    }

    @Test
    @DisplayName("register() should throw DuplicateEntryException when email is taken")
    void register_shouldThrowException_whenEmailExists() {
        // Arrange
        String email = "duplicate@example.com";
        userService.register(createDefaultRequest(email));
        UserRequestDTO secondRequest = createDefaultRequest(email);

        // Act & Assert
        assertThatThrownBy(() -> userService.register(secondRequest))
                .isInstanceOf(DuplicateEntryException.class);
    }

    @Test
    @DisplayName("findById() should return user when exists")
    void findById_shouldReturnUser() {
        // Arrange
        UserResponseDTO registered = userService.register(createDefaultRequest("findme@example.com"));

        // Act
        Optional<UserResponseDTO> found = userService.findById(registered.id());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(registered.id());
        assertThat(found.get().email()).isEqualTo("findme@example.com");
    }

    @Test
    @DisplayName("findById() should return empty when not found")
    void findById_shouldReturnEmpty_whenNotFound() {
        // Act
        Optional<UserResponseDTO> found = userService.findById(999L);

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("findAll() should return all users")
    void findAll_shouldReturnAll() {
        // Arrange
        userService.register(createDefaultRequest("user1@example.com"));
        userService.register(createDefaultRequest("user2@example.com"));

        // Act
        List<UserResponseDTO> users = userService.findAll();

        // Assert
        assertThat(users).hasSizeGreaterThanOrEqualTo(2);
        assertThat(users).extracting(UserResponseDTO::email)
                .contains("user1@example.com", "user2@example.com");
    }

    @Test
    @DisplayName("deleteById() should remove user when exists")
    void deleteById_shouldRemoveUser() {
        // Arrange
        UserResponseDTO registered = userService.register(createDefaultRequest("delete@example.com"));
        Long id = registered.id();

        // Act
        userService.deleteById(id);

        // Assert
        assertThat(userService.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("deleteById() should throw DataNotFoundException when not found")
    void deleteById_shouldThrowException_whenNotFound() {
        // Act & Assert
        assertThatThrownBy(() -> userService.deleteById(999L))
                .isInstanceOf(DataNotFoundException.class);
    }
}
