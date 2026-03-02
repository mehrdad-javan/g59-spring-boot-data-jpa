package se.lexicon.g59springbootdatajpa.dto.response;

import java.time.Instant;

public record UserResponseDTO(
        Long id,
        String email,
        String fullName,
        Instant createDate
) {
}
