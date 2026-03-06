package se.lexicon.g59springbootdatajpa.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.lexicon.g59springbootdatajpa.dto.request.UserRequestDTO;
import se.lexicon.g59springbootdatajpa.dto.response.UserResponseDTO;
import se.lexicon.g59springbootdatajpa.entity.User;
import se.lexicon.g59springbootdatajpa.exception.DataNotFoundException;
import se.lexicon.g59springbootdatajpa.exception.DuplicateEntryException;
import se.lexicon.g59springbootdatajpa.mapper.EntityToDtoMapper;
import se.lexicon.g59springbootdatajpa.repository.UserRepository;
import se.lexicon.g59springbootdatajpa.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EntityToDtoMapper mapper;

    public UserServiceImpl(UserRepository userRepository, EntityToDtoMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public UserResponseDTO register(UserRequestDTO userRequestDto) {
        if (userRequestDto == null) throw new IllegalArgumentException("User Request cannot be null");
        if (userRepository.existsByEmail(userRequestDto.email())) {
            throw new DuplicateEntryException("User with email already exists");
        }
        User user = mapper.toUserEntity(userRequestDto);
        User savedUser = userRepository.save(user);
        return mapper.toUserResponseDTO(savedUser);
    }

    @Override
    @Transactional
    public UserResponseDTO update(Long id, UserRequestDTO userRequestDto) {
        if (userRequestDto == null) throw new IllegalArgumentException("User Request cannot be null");

        User user;
        if (id != null) {
            user = userRepository.findById(id).orElseGet(() -> mapper.toUserEntity(userRequestDto));
        } else {
            user = mapper.toUserEntity(userRequestDto);
        }

        if (!userRequestDto.email().equals(user.getEmail()) && userRepository.existsByEmail(userRequestDto.email())) {
            throw new DuplicateEntryException("User with email already exists");
        }

        user.setEmail(userRequestDto.email());
        user.setFullName(userRequestDto.fullName());

        User updatedUser = userRepository.save(user);
        return mapper.toUserResponseDTO(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> findById(Long id) {
        return userRepository.findById(id).map(mapper::toUserResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(mapper::toUserResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (id == null) throw new IllegalArgumentException("User ID cannot be null");
        if (!userRepository.existsById(id)) {
            throw new DataNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }
}
