package se.lexicon.g59springbootdatajpa.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.lexicon.g59springbootdatajpa.dto.request.UserProfileRequestDTO;
import se.lexicon.g59springbootdatajpa.dto.response.UserProfileResponseDTO;
import se.lexicon.g59springbootdatajpa.entity.UserProfile;
import se.lexicon.g59springbootdatajpa.mapper.EntityToDtoMapper;
import se.lexicon.g59springbootdatajpa.repository.UserProfileRepository;
import se.lexicon.g59springbootdatajpa.service.UserProfileService;

import java.util.Optional;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final EntityToDtoMapper mapper;

    public UserProfileServiceImpl(UserProfileRepository userProfileRepository, EntityToDtoMapper mapper) {
        this.userProfileRepository = userProfileRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public UserProfileResponseDTO update(Long id, UserProfileRequestDTO profileRequestDto) {
        if (profileRequestDto == null) throw new IllegalArgumentException("Profile request cannot be null.");

        UserProfile profile;
        if (id != null) {
            profile = userProfileRepository.findById(id).orElseGet(() -> mapper.toUserProfileEntity(profileRequestDto));
        } else {
            profile = mapper.toUserProfileEntity(profileRequestDto);
        }

        profile.setNickname(profileRequestDto.nickname());
        profile.setPhoneNumber(profileRequestDto.phoneNumber());
        profile.setBio(profileRequestDto.bio());
        profile.setAddress(profileRequestDto.address());

        UserProfile updatedProfile = userProfileRepository.save(profile);
        return mapper.toUserProfileResponseDTO(updatedProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserProfileResponseDTO> findById(Long id) {
        return userProfileRepository.findById(id).map(mapper::toUserProfileResponseDTO);
    }
}
