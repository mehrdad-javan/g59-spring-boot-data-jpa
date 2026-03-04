package se.lexicon.g59springbootdatajpa.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.lexicon.g59springbootdatajpa.dto.request.UserRequestDTO;
import se.lexicon.g59springbootdatajpa.dto.response.UserResponseDTO;
import se.lexicon.g59springbootdatajpa.entity.User;
import se.lexicon.g59springbootdatajpa.exception.DataNotFoundException;
import se.lexicon.g59springbootdatajpa.exception.DuplicateEntryException;
import se.lexicon.g59springbootdatajpa.mapper.EntityToDtoMapper;
import se.lexicon.g59springbootdatajpa.repository.UserRepository;
import se.lexicon.g59springbootdatajpa.service.impl.UserServiceImpl;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntityToDtoMapper mapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequestDTO userRequestDTO;
    private User user;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        userRequestDTO = new UserRequestDTO("test@example.com", "Test User");
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        // createDate is usually set by @PrePersist, but for mocking we might need it
        
        userResponseDTO = new UserResponseDTO(1L, "test@example.com", "Test User", Instant.now());
    }

    @Test
    @DisplayName("register() should save user and return response")
    void register_success() {
        // Arrange
        when(userRepository.existsByEmail(userRequestDTO.email())).thenReturn(false);
        when(mapper.toUserEntity(userRequestDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(mapper.toUserResponseDTO(user)).thenReturn(userResponseDTO);

        // Act
        UserResponseDTO result = userService.register(userRequestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(userRequestDTO.email());
        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("register() should throw IllegalArgumentException when request is null")
    void register_nullRequest() {
        // Act & Assert
        assertThatThrownBy(() -> userService.register(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User Request cannot be null");
    }

    @Test
    @DisplayName("register() should throw DuplicateEntryException when email already exists")
    void register_duplicateEmail() {
        // Arrange
        when(userRepository.existsByEmail(userRequestDTO.email())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.register(userRequestDTO))
                .isInstanceOf(DuplicateEntryException.class)
                .hasMessageContaining("User with email already exists");
        
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("findById() should return UserResponseDTO when user exists")
    void findById_found() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(mapper.toUserResponseDTO(user)).thenReturn(userResponseDTO);

        // Act
        Optional<UserResponseDTO> result = userService.findById(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("findById() should return empty Optional when user not found")
    void findById_notFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<UserResponseDTO> result = userService.findById(1L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAll() should return list of UserResponseDTO")
    void findAll_success() {
        // Arrange
        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);
        when(mapper.toUserResponseDTO(user)).thenReturn(userResponseDTO);

        // Act
        List<UserResponseDTO> result = userService.findAll();

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("deleteById() should delete user when user exists")
    void deleteById_success() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);

        // Act
        userService.deleteById(1L);

        // Assert
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteById() should throw IllegalArgumentException when ID is null")
    void deleteById_nullId() {
        // Act & Assert
        assertThatThrownBy(() -> userService.deleteById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID cannot be null");
    }

    @Test
    @DisplayName("deleteById() should throw DataNotFoundException when user not found")
    void deleteById_notFound() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> userService.deleteById(1L))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("User not found with ID: 1");

        verify(userRepository, never()).deleteById(anyLong());
    }
}
