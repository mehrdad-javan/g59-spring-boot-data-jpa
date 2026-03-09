package se.lexicon.g59springbootdatajpa.controller;

/**
 * WEB LAYER TEST (MockMvc)
 * -----------------------
 * This class tests the 'UserController' in a Spring context but with the
 * database layer mocked.
 * <p>
 * WHAT IS A WEB LAYER TEST?
 * It focuses on the communication between the client and the controller.
 * It verifies:
 * 1. Routing: Does the URL 'GET /api/v1/users' trigger the correct method?
 * 2. Serialization: Is the Java object correctly converted to JSON?
 * 3. HTTP Status: Does the API return 200, 201, 400, 404, etc., correctly?
 * 4. Validation: Does @Valid catch bad input before it reaches the service?
 * <p>
 * KEY ANNOTATIONS:
 * - @SpringBootTest: Loads the application context.
 * - @AutoConfigureMockMvc: Injects the MockMvc tool to simulate HTTP requests.
 * - @MockitoBean: Creates a mock of a Spring bean (replaces the real one in the context).
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import se.lexicon.g59springbootdatajpa.dto.request.UserRequestDTO;
import se.lexicon.g59springbootdatajpa.entity.User;
import se.lexicon.g59springbootdatajpa.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private UserRequestDTO createDefaultRequest(String email) {
        return new UserRequestDTO(email, "John Doe");
    }

    @Test
    @DisplayName("POST /api/v1/users should register user and return 201")
    void register_shouldSucceed() throws Exception {
        // Arrange
        UserRequestDTO request = createDefaultRequest("test@example.com");
        User user = new User();
        user.setId(1L);
        user.setEmail(request.email());
        user.setFullName(request.fullName());

        // when(...).thenReturn(): This is the core of Mockito's 'stubbing' mechanism.
        // It tells the mock object how to behave when a specific method is called.
        // - 'when(userRepository.existsByEmail(request.email()))': Intercepts calls to this method with this exact email.
        // - '.thenReturn(false)': Defines that the mock should return 'false' (simulating that the email is not taken).
        when(userRepository.existsByEmail(request.email())).thenReturn(false);

        // - 'when(userRepository.save(any(User.class)))': Intercepts calls to 'save' with ANY User object.
        //   - 'any(User.class)': This is an 'Argument Matcher'. It means we don't care about the
        //     exact User instance being passed, as long as it's of the User class.
        //     Other examples: anyString(), anyLong(), anyInt(), or even 'isNull()'.
        // - '.thenReturn(user)': Defines that the mock should return our prepared 'user' object (with ID 1).
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act & Assert
        // mockMvc.perform(): Sends the HTTP request.
        // post("/api/v1/users"): The HTTP method (POST) and URL.
        // .contentType(MediaType.APPLICATION_JSON): Tells the server we are sending JSON data.
        // .content(objectMapper.writeValueAsString(request)): The actual JSON body (converted from a Java object).
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // .andExpect(): A way to assert the results.
                // .status().isCreated(): Checks if the server returned '201 Created'.
                .andExpect(status().isCreated())
                // .jsonPath("$.id"): Uses 'JsonPath' to inspect the response body.
                // '$.id' looks for a field named 'id' at the root of the JSON.
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.fullName").value("John Doe"));

        // verify(): Checks if the repository's save method was called.
        // - userRepository: The mock object we are checking.
        // - atLeastOnce(): Ensures the method was called 1 or more times.
        // - .save(any(User.class)): Verifies that it was called with ANY User object as an argument.
        //   - Argument Matchers like 'any()' are useful here because the object passed to 'save'
        //     is created inside the controller/service and we don't have its exact reference.
        verify(userRepository, atLeastOnce()).save(any(User.class));
    }

    @Test
    @DisplayName("POST /api/v1/users should return 409 when email exists")
    void register_shouldReturn409_whenEmailExists() throws Exception {
        // Arrange
        UserRequestDTO request = createDefaultRequest("duplicate@example.com");
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} should return user")
    void findById_shouldSucceed() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setEmail("mock@example.com");
        user.setFullName("Test Testsson");

        // when(...).thenReturn(): Simulates a database 'hit' where the user is found.
        // - 'Optional.of(user)': Wraps our mock user in an Optional, as the repository method returns Optional.
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act & Assert
        // get("/api/v1/users/1"): The HTTP method (GET) and URL with an ID.
        mockMvc.perform(get("/api/v1/users/1"))
                // .status().isOk(): Checks if the server returned '200 OK'.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("mock@example.com"));

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} should return 404 when not found")
    void findById_shouldReturn404() throws Exception {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/users should return all users")
    void findAll_shouldReturn200_andList() throws Exception {
        // Arrange
        User u1 = new User();
        u1.setId(1L);
        u1.setEmail("user1@example.com");
        u1.setFullName("User One");
        User u2 = new User();
        u2.setId(2L);
        u2.setEmail("user2@example.com");
        u2.setFullName("User Two");

        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                // .jsonPath("$.length()"): Verifies how many items are in the JSON array.
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").value("user1@example.com"))
                .andExpect(jsonPath("$[1].email").value("user2@example.com"));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} should remove user")
    void deleteById_shouldSucceed() throws Exception {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // Act & Assert
        // delete("/api/v1/users/1"): The HTTP method (DELETE) and URL.
        mockMvc.perform(delete("/api/v1/users/1"))
                // .status().isNoContent(): Checks if the server returned '204 No Content'.
                .andExpect(status().isNoContent());

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} should return 404 when not found")
    void deleteById_shouldReturn404() throws Exception {
        // Arrange
        when(userRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/users/99"))
                .andExpect(status().isNotFound());
    }
}
