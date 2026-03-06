package se.lexicon.g59springbootdatajpa.service;

import se.lexicon.g59springbootdatajpa.dto.request.EventRequestDTO;
import se.lexicon.g59springbootdatajpa.dto.response.EventResponseDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventService {

    EventResponseDTO create(EventRequestDTO eventRequestDto);

    Optional<EventResponseDTO> findById(Long id);

    List<EventResponseDTO> findAll();

    EventResponseDTO update(Long id, EventRequestDTO eventRequestDto);

    void deleteById(Long id);

    List<EventResponseDTO> findByTitle(String title);

    List<EventResponseDTO> findByDateRange(LocalDateTime start, LocalDateTime end);

    List<EventResponseDTO> findByLocation(String location);

    List<EventResponseDTO> findByStatus(String status);

    void addParticipant(Long eventId, Long userId);

    void removeParticipant(Long eventId, Long userId);
}
