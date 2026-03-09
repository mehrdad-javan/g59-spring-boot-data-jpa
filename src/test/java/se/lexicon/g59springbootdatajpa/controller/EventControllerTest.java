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
import se.lexicon.g59springbootdatajpa.dto.request.EventRequestDTO;
import se.lexicon.g59springbootdatajpa.entity.Event;
import se.lexicon.g59springbootdatajpa.entity.EventStatus;
import se.lexicon.g59springbootdatajpa.entity.User;
import se.lexicon.g59springbootdatajpa.repository.EventRepository;
import se.lexicon.g59springbootdatajpa.repository.UserRepository;

import java.time.LocalDateTime;
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
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventRepository eventRepository;

    @MockitoBean
    private UserRepository userRepository;

    private ObjectMapper objectMapper;

    private User creator;
    private Event event;
    private EventRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        creator = new User();
        creator.setId(1L);
        creator.setEmail("creator@test.com");

        event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");
        event.setDateTime(LocalDateTime.now().plusDays(1));
        event.setStatus(EventStatus.PLANNED);
        event.setCreatedBy(creator);

        requestDTO = new EventRequestDTO(
                "Test Event",
                "Description",
                LocalDateTime.now().plusDays(1),
                "Location",
                "PLANNED",
                1L
        );
    }

    @Test
    @DisplayName("POST /api/v1/events should create and return 201")
    void create_shouldSucceed() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Event"));

        verify(eventRepository, atLeastOnce()).save(any(Event.class));
    }

    @Test
    @DisplayName("GET /api/v1/events/{id} should return event")
    void findById_shouldSucceed() throws Exception {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        mockMvc.perform(get("/api/v1/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Event"));
    }

    @Test
    @DisplayName("GET /api/v1/events should return all events")
    void findAll_shouldReturnList() throws Exception {
        when(eventRepository.findAll()).thenReturn(List.of(event));

        mockMvc.perform(get("/api/v1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("PUT /api/v1/events/{id} should update and return 200")
    void update_shouldSucceed() throws Exception {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        mockMvc.perform(put("/api/v1/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Event"));
    }

    @Test
    @DisplayName("DELETE /api/v1/events/{id} should return 204")
    void delete_shouldSucceed() throws Exception {
        when(eventRepository.existsById(1L)).thenReturn(true);
        doNothing().when(eventRepository).deleteById(1L);

        mockMvc.perform(delete("/api/v1/events/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/v1/events/search/title should return events")
    void findByTitle_shouldSucceed() throws Exception {
        when(eventRepository.findByTitleContaining("Test")).thenReturn(List.of(event));

        // Act & Assert
        // .param(): Adds a query parameter to the URL (e.g., ?keyword=Test).
        mockMvc.perform(get("/api/v1/events/search/title").param("keyword", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Event"));
    }

    @Test
    @DisplayName("POST /api/v1/events/{eventId}/participants/{userId} should return 201")
    void addParticipant_shouldSucceed() throws Exception {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        // Act & Assert
        // post(): HTTP POST method for adding a relationship.
        mockMvc.perform(post("/api/v1/events/1/participants/1"))
                // status().isCreated(): Checks for 201 Created.
                .andExpect(status().isCreated());

        verify(eventRepository, atLeastOnce()).save(any(Event.class));
    }
}
