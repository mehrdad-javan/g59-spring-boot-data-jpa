package se.lexicon.g59springbootdatajpa.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.lexicon.g59springbootdatajpa.dto.request.EventRequestDTO;
import se.lexicon.g59springbootdatajpa.dto.response.EventResponseDTO;
import se.lexicon.g59springbootdatajpa.dto.response.UserResponseDTO;
import se.lexicon.g59springbootdatajpa.entity.Event;
import se.lexicon.g59springbootdatajpa.entity.EventStatus;
import se.lexicon.g59springbootdatajpa.entity.User;
import se.lexicon.g59springbootdatajpa.exception.DataNotFoundException;
import se.lexicon.g59springbootdatajpa.mapper.EntityToDtoMapper;
import se.lexicon.g59springbootdatajpa.repository.EventRepository;
import se.lexicon.g59springbootdatajpa.repository.UserRepository;
import se.lexicon.g59springbootdatajpa.service.impl.EventServiceImpl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntityToDtoMapper mapper;

    @InjectMocks
    private EventServiceImpl eventService;

    private User creator;
    private Event event;
    private EventRequestDTO eventRequestDTO;
    private EventResponseDTO eventResponseDTO;

    @BeforeEach
    void setUp() {
        creator = new User();
        creator.setId(1L);
        creator.setEmail("creator@test.com");

        event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");
        event.setDateTime(LocalDateTime.now().plusDays(1));
        event.setStatus(EventStatus.PLANNED);
        event.setCreatedBy(creator);

        eventRequestDTO = new EventRequestDTO(
                "Test Event",
                "Description",
                LocalDateTime.now().plusDays(1),
                "Location",
                "PLANNED",
                1L
        );

        UserResponseDTO creatorResponse = new UserResponseDTO(1L, "creator@test.com", "Creator", Instant.now());
        eventResponseDTO = new EventResponseDTO(
                1L,
                "Test Event",
                "Description",
                event.getDateTime(),
                "Location",
                "PLANNED",
                creatorResponse,
                Collections.emptySet()
        );
    }

    @Test
    @DisplayName("update() should update event when event exists")
    void update_success() {
        // Arrange
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(mapper.toEventResponseDTO(event)).thenReturn(eventResponseDTO);

        // Act
        EventResponseDTO result = eventService.update(1L, eventRequestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Test Event");
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("update() should create new event when ID not found (upsert)")
    void update_upsert_new() {
        // Arrange
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        when(mapper.toEventEntity(eventRequestDTO)).thenReturn(event);
        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(mapper.toEventResponseDTO(event)).thenReturn(eventResponseDTO);

        // Act
        EventResponseDTO result = eventService.update(1L, eventRequestDTO);

        // Assert
        assertThat(result).isNotNull();
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("create() should throw DataNotFoundException when creator not found")
    void create_creatorNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> eventService.create(eventRequestDTO))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Creator user not found with ID: 1");
    }
}