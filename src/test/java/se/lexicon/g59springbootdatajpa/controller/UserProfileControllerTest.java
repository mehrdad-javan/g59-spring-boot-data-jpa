package se.lexicon.g59springbootdatajpa.controller;

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
import se.lexicon.g59springbootdatajpa.dto.request.UserProfileRequestDTO;
import se.lexicon.g59springbootdatajpa.entity.UserProfile;
import se.lexicon.g59springbootdatajpa.repository.UserProfileRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserProfileRepository userProfileRepository;

    private ObjectMapper objectMapper;

    private UserProfile profile;
    private UserProfileRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        profile = new UserProfile();
        profile.setId(1L);
        profile.setNickname("OldNick");

        requestDTO = new UserProfileRequestDTO("NewNick", "123456", "Bio", "Address");
    }

    @Test
    @DisplayName("GET /api/v1/profiles/{id} should return profile")
    void findById_shouldSucceed() throws Exception {
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(profile));

        // Act & Assert
        // mockMvc.perform(): Sends the request.
        // get(): HTTP GET method.
        mockMvc.perform(get("/api/v1/profiles/1"))
                // .andExpect(): Asserts the response.
                // status().isOk(): Expects 200 OK.
                .andExpect(status().isOk())
                // jsonPath(): Inspects the JSON body.
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nickname").value("OldNick"));
    }

    @Test
    @DisplayName("GET /api/v1/profiles/{id} should return 404 when not found")
    void findById_shouldReturn404() throws Exception {
        when(userProfileRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/profiles/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/v1/profiles/{id} should update and return 200")
    void update_shouldSucceed() throws Exception {
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(profile);

        // Act & Assert
        // put(): HTTP PUT method (full update).
        // .contentType(): Set request header.
        // .content(): Set request body as JSON string.
        mockMvc.perform(put("/api/v1/profiles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("NewNick"));

        verify(userProfileRepository, atLeastOnce()).save(any(UserProfile.class));
    }
}
