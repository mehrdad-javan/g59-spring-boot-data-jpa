package se.lexicon.g59springbootdatajpa.service;

import se.lexicon.g59springbootdatajpa.dto.request.UserRequestDTO;
import se.lexicon.g59springbootdatajpa.dto.response.UserResponseDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserResponseDTO register(UserRequestDTO userRequestDto);

    UserResponseDTO update(Long id, UserRequestDTO userRequestDto);

    Optional<UserResponseDTO> findById(Long id);

    List<UserResponseDTO> findAll();

    void deleteById(Long id);

}
