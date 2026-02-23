package se.lexicon.g59springbootdatajpa.reposiroty;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import se.lexicon.g59springbootdatajpa.entity.Event;
import se.lexicon.g59springbootdatajpa.entity.EventStatus;
import se.lexicon.g59springbootdatajpa.entity.User;
import se.lexicon.g59springbootdatajpa.repository.EventRepository;
import se.lexicon.g59springbootdatajpa.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    private User creator;
    private User participant;

    @BeforeEach
    void setUp() {
        creator = new User();
        creator.setEmail("creator@test.com");
        creator.setFullName("Test Creator");
        userRepository.save(creator);

        participant = new User();
        participant.setEmail("participant@test.com");
        participant.setFullName("Test Participant");
        userRepository.save(participant);
    }

    @Test
    @DisplayName("save() should persist event with creator and participants")
    void save_shouldPersistEvent() {
        // Arrange
        Event event = new Event();
        event.setTitle("Workshop");
        event.setDescription("Java Programming Workshop");
        event.setDateTime(LocalDateTime.now().plusDays(1));
        event.setLocation("Online");
        event.setCreatedBy(creator);
        event.addParticipant(participant);

        // Act
        Event saved = eventRepository.save(event);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedBy()).isEqualTo(creator);
        assertThat(saved.getParticipants()).contains(participant);
    }

    @Test
    @DisplayName("findByTitleContaining() should return events matching title")
    void findByTitleContaining_shouldReturnMatches() {
        // Arrange
        Event event1 = new Event();
        event1.setTitle("Spring Boot");
        event1.setDateTime(LocalDateTime.now());
        event1.setCreatedBy(creator);

        Event event2 = new Event();
        event2.setTitle("Java JPA");
        event2.setDateTime(LocalDateTime.now());
        event2.setCreatedBy(creator);

        eventRepository.save(event1);
        eventRepository.save(event2);

        // Act
        List<Event> result = eventRepository.findByTitleContaining("Spring");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Spring Boot");
    }

    @Test
    @DisplayName("findByDateTimeBetween() should return events within range")
    void findByDateTimeBetween_shouldReturnMatches() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Event event = new Event();
        event.setTitle("Meeting");
        event.setDateTime(now.plusHours(2));
        event.setCreatedBy(creator);
        eventRepository.save(event);

        // Act
        List<Event> result = eventRepository.findByDateTimeBetween(now, now.plusHours(5));

        // Assert
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("findByLocation() should return events at location")
    void findByLocation_shouldReturnMatches() {
        // Arrange
        Event event = new Event();
        event.setTitle("Conference");
        event.setDateTime(LocalDateTime.now());
        event.setLocation("Stockholm");
        event.setCreatedBy(creator);
        eventRepository.save(event);

        // Act
        List<Event> result = eventRepository.findByLocation("Stockholm");

        // Assert
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("removeParticipant() should update participants set")
    void removeParticipant_shouldUpdateSet() {
        // Arrange
        Event event = new Event();
        event.setTitle("Test");
        event.setDateTime(LocalDateTime.now());
        event.setCreatedBy(creator);
        event.addParticipant(participant);
        event = eventRepository.save(event);

        // Act
        event.removeParticipant(participant);
        Event updated = eventRepository.save(event);

        // Assert
        assertThat(updated.getParticipants()).isEmpty();
    }

    @Test
    @DisplayName("findByStatus() should return events with status")
    void findByStatus_shouldReturnMatches() {
        // Arrange
        Event event = new Event();
        event.setTitle("Status Test");
        event.setDateTime(LocalDateTime.now());
        event.setCreatedBy(creator);
        event.setStatus(EventStatus.CANCELLED);
        eventRepository.save(event);

        // Act
        List<Event> result = eventRepository.findByStatus(EventStatus.CANCELLED);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(EventStatus.CANCELLED);
    }
}
