package se.lexicon.g59springbootdatajpa.service;

import se.lexicon.g59springbootdatajpa.dto.request.UserProfileRequestDTO;
import se.lexicon.g59springbootdatajpa.dto.response.UserProfileResponseDTO;

import java.util.Optional;

public interface UserProfileService {
    UserProfileResponseDTO update(Long id, UserProfileRequestDTO profileRequestDto);
    Optional<UserProfileResponseDTO> findById(Long id);
}
