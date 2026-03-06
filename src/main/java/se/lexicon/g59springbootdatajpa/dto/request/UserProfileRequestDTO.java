package se.lexicon.g59springbootdatajpa.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserProfileRequestDTO(
        @Size(max = 100)
        String nickname,
        @Size(max = 20)
        String phoneNumber,
        @Size(max = 500)
        String bio,
        @Size(max = 255)
        String address
) {
}
