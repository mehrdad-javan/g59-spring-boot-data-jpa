package se.lexicon.g59springbootdatajpa.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.lexicon.g59springbootdatajpa.dto.request.UserProfileRequestDTO;
import se.lexicon.g59springbootdatajpa.dto.response.UserProfileResponseDTO;
import se.lexicon.g59springbootdatajpa.entity.UserProfile;
import se.lexicon.g59springbootdatajpa.mapper.EntityToDtoMapper;
import se.lexicon.g59springbootdatajpa.repository.UserProfileRepository;
import se.lexicon.g59springbootdatajpa.service.impl.UserProfileServiceImpl;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceImplTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private EntityToDtoMapper mapper;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    private UserProfileRequestDTO profileRequestDTO;
    private UserProfile profile;
    private UserProfileResponseDTO profileResponseDTO;

    @BeforeEach
    void setUp() {
        profileRequestDTO = new UserProfileRequestDTO("nick", "123456", "bio", "address");
        profile = new UserProfile();
        profile.setId(1L);
        profile.setNickname("nick");

        profileResponseDTO = new UserProfileResponseDTO(1L, "nick", "123456", "bio", "address");
    }

    @Test
    @DisplayName("update() should update profile when it exists")
    void update_success() {
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(profile);
        when(mapper.toUserProfileResponseDTO(profile)).thenReturn(profileResponseDTO);

        UserProfileResponseDTO result = userProfileService.update(1L, profileRequestDTO);

        assertThat(result).isNotNull();
        assertThat(result.nickname()).isEqualTo("nick");
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }

    @Test
    @DisplayName("update() should create profile when ID not found")
    void update_notFound_create() {
        when(userProfileRepository.findById(1L)).thenReturn(Optional.empty());
        when(mapper.toUserProfileEntity(profileRequestDTO)).thenReturn(profile);
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(profile);
        when(mapper.toUserProfileResponseDTO(profile)).thenReturn(profileResponseDTO);

        UserProfileResponseDTO result = userProfileService.update(1L, profileRequestDTO);

        assertThat(result).isNotNull();
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }
}
