package se.lexicon.g59springbootdatajpa.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.lexicon.g59springbootdatajpa.dto.request.EventRequestDTO;
import se.lexicon.g59springbootdatajpa.dto.response.EventResponseDTO;
import se.lexicon.g59springbootdatajpa.entity.Event;
import se.lexicon.g59springbootdatajpa.entity.EventStatus;
import se.lexicon.g59springbootdatajpa.entity.User;
import se.lexicon.g59springbootdatajpa.exception.DataNotFoundException;
import se.lexicon.g59springbootdatajpa.mapper.EntityToDtoMapper;
import se.lexicon.g59springbootdatajpa.repository.EventRepository;
import se.lexicon.g59springbootdatajpa.repository.UserRepository;
import se.lexicon.g59springbootdatajpa.service.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EntityToDtoMapper mapper;

    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository, EntityToDtoMapper mapper) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public EventResponseDTO create(EventRequestDTO eventRequestDto) {
        if (eventRequestDto == null) throw new IllegalArgumentException("Event request cannot be null.");
        User creator = userRepository.findById(eventRequestDto.createdByUserId())
                .orElseThrow(() -> new DataNotFoundException("Creator user not found with ID: " + eventRequestDto.createdByUserId()));

        Event event = mapper.toEventEntity(eventRequestDto);
        event.setCreatedBy(creator);

        Event savedEvent = eventRepository.save(event);
        return mapper.toEventResponseDTO(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EventResponseDTO> findById(Long id) {
        return eventRepository.findById(id).map(mapper::toEventResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDTO> findAll() {
        return eventRepository.findAll().stream().map(mapper::toEventResponseDTO).toList();
    }

    @Override
    @Transactional
    public EventResponseDTO update(Long id, EventRequestDTO eventRequestDto) {
        if (eventRequestDto == null) throw new IllegalArgumentException("Event request cannot be null.");

        Event event;
        if (id != null) {
            event = eventRepository.findById(id).orElseGet(() -> mapper.toEventEntity(eventRequestDto));
        } else {
            event = mapper.toEventEntity(eventRequestDto);
        }

        User creator = userRepository.findById(eventRequestDto.createdByUserId())
                .orElseThrow(() -> new DataNotFoundException("Creator user not found with ID: " + eventRequestDto.createdByUserId()));

        event.setTitle(eventRequestDto.title());
        event.setDescription(eventRequestDto.description());
        event.setDateTime(eventRequestDto.dateTime());
        event.setLocation(eventRequestDto.location());
        event.setStatus(EventStatus.fromString(eventRequestDto.status()));
        event.setCreatedBy(creator);

        Event savedEvent = eventRepository.save(event);
        return mapper.toEventResponseDTO(savedEvent);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (id == null) throw new IllegalArgumentException("Event ID cannot be null.");
        if (!eventRepository.existsById(id)) {
            throw new DataNotFoundException("Event not found with ID: " + id);
        }
        eventRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDTO> findByTitle(String title) {
        return eventRepository.findByTitleContaining(title).stream()
                .map(mapper::toEventResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDTO> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return eventRepository.findByDateTimeBetween(start, end).stream()
                .map(mapper::toEventResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDTO> findByLocation(String location) {
        return eventRepository.findByLocation(location).stream()
                .map(mapper::toEventResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDTO> findByStatus(String status) {
        return eventRepository.findByStatus(EventStatus.fromString(status)).stream()
                .map(mapper::toEventResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void addParticipant(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("Event not found with ID: " + eventId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found with ID: " + userId));

        event.addParticipant(user);
        eventRepository.save(event);
    }

    @Override
    @Transactional
    public void removeParticipant(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("Event not found with ID: " + eventId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found with ID: " + userId));

        event.removeParticipant(user);
        eventRepository.save(event);
    }
}
