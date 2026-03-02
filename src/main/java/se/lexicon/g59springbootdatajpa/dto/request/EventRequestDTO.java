package se.lexicon.g59springbootdatajpa.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record EventRequestDTO(
        @NotBlank(message = "Title cannot be blank")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        @NotNull(message = "Date and time cannot be null")
        @FutureOrPresent(message = "Date and time must be in the future")
        LocalDateTime dateTime,

        @NotBlank(message = "Location cannot be blank")
        @Size(max = 255, message = "Location must not exceed 255 characters")
        String location,

        @NotBlank(message = "Status is required")
        String status,

        @NotNull(message = "Created by user ID is required")
        Long createdByUserId) {
}
