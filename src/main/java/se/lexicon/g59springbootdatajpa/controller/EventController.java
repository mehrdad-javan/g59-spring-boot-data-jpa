package se.lexicon.g59springbootdatajpa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import se.lexicon.g59springbootdatajpa.dto.request.EventRequestDTO;
import se.lexicon.g59springbootdatajpa.dto.response.EventResponseDTO;
import se.lexicon.g59springbootdatajpa.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@Validated
@Tag(name = "Event Controller", description = "APIs for managing events and participants")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    @Operation(summary = "Create a new event")
    @Tag(name = "Event Operations")
    public ResponseEntity<EventResponseDTO> create(@Valid @RequestBody EventRequestDTO eventRequestDto) {
        EventResponseDTO createdEvent = eventService.create(eventRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find an event by ID")
    @Tag(name = "Event Operations")
    public ResponseEntity<EventResponseDTO> findById(@PathVariable @Positive Long id) {
        return eventService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "List all events")
    @Tag(name = "Event Operations")
    public ResponseEntity<List<EventResponseDTO>> findAll() {
        return ResponseEntity.ok(eventService.findAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an event")
    @Tag(name = "Event Operations")
    public ResponseEntity<EventResponseDTO> update(@PathVariable @Positive Long id, @Valid @RequestBody EventRequestDTO eventRequestDto) {
        EventResponseDTO updatedEvent = eventService.update(id, eventRequestDto);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an event")
    @Tag(name = "Event Operations")
    public ResponseEntity<Void> deleteById(@PathVariable @Positive Long id) {
        eventService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/title")
    @Operation(summary = "Search events by title keyword")
    @Tag(name = "Search Operations")
    public ResponseEntity<List<EventResponseDTO>> findByTitle(@RequestParam @NotBlank String keyword) {
        return ResponseEntity.ok(eventService.findByTitle(keyword));
    }

    @GetMapping("/search/date")
    @Operation(summary = "Search events by date range")
    @Tag(name = "Search Operations")
    public ResponseEntity<List<EventResponseDTO>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(eventService.findByDateRange(start, end));
    }

    @GetMapping("/search/location")
    @Operation(summary = "Search events by location")
    @Tag(name = "Search Operations")
    public ResponseEntity<List<EventResponseDTO>> findByLocation(@RequestParam @NotBlank String location) {
        return ResponseEntity.ok(eventService.findByLocation(location));
    }

    @GetMapping("/search/status")
    @Operation(summary = "Search events by status")
    @Tag(name = "Search Operations")
    public ResponseEntity<List<EventResponseDTO>> findByStatus(@RequestParam @NotBlank String status) {
        return ResponseEntity.ok(eventService.findByStatus(status));
    }

    @PostMapping("/{eventId}/participants/{userId}")
    @Operation(summary = "Add a participant to an event")
    @Tag(name = "Participant Operations")
    public ResponseEntity<Void> addParticipant(@PathVariable @Positive Long eventId, @PathVariable @Positive Long userId) {
        eventService.addParticipant(eventId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{eventId}/participants/{userId}")
    @Operation(summary = "Remove a participant from an event")
    @Tag(name = "Participant Operations")
    public ResponseEntity<Void> removeParticipant(@PathVariable @Positive Long eventId, @PathVariable @Positive Long userId) {
        eventService.removeParticipant(eventId, userId);
        return ResponseEntity.noContent().build();
    }
}
