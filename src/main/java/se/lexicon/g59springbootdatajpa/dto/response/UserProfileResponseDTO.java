package se.lexicon.g59springbootdatajpa.dto.response;

public record UserProfileResponseDTO(
        Long id,
        String nickname,
        String phoneNumber,
        String bio,
        String address
) {
}
