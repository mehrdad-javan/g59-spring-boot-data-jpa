package se.lexicon.g59springbootdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.lexicon.g59springbootdatajpa.entity.Event;
import se.lexicon.g59springbootdatajpa.entity.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByTitleContaining(String title);

    List<Event> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Event> findByLocation(String location);

    List<Event> findByStatus(EventStatus status);
}
