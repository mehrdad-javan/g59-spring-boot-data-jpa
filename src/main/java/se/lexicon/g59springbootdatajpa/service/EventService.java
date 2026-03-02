package se.lexicon.g59springbootdatajpa.service;

import se.lexicon.g59springbootdatajpa.dto.request.EventRequestDTO;
import se.lexicon.g59springbootdatajpa.dto.response.EventResponseDTO;

import java.util.List;
import java.util.Optional;

public interface EventService {

    EventResponseDTO create(EventRequestDTO eventRequestDTO);

    Optional<EventResponseDTO> findById(Long id);

    List<EventResponseDTO> findAll();

    void addParticipant(Long eventId, Long participantId);

    void removeParticipant(Long eventId, Long participantId);

    List<EventResponseDTO> findByStatus(String status);

    // add more methods as needed
}
