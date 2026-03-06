package se.lexicon.g59springbootdatajpa.mapper;

import org.springframework.stereotype.Component;
import se.lexicon.g59springbootdatajpa.dto.request.EventRequestDTO;
import se.lexicon.g59springbootdatajpa.dto.request.UserProfileRequestDTO;
import se.lexicon.g59springbootdatajpa.dto.request.UserRequestDTO;
import se.lexicon.g59springbootdatajpa.dto.response.EventResponseDTO;
import se.lexicon.g59springbootdatajpa.dto.response.UserProfileResponseDTO;
import se.lexicon.g59springbootdatajpa.dto.response.UserResponseDTO;
import se.lexicon.g59springbootdatajpa.entity.Event;
import se.lexicon.g59springbootdatajpa.entity.EventStatus;
import se.lexicon.g59springbootdatajpa.entity.User;
import se.lexicon.g59springbootdatajpa.entity.UserProfile;

import java.util.stream.Collectors;

@Component
public class EntityToDtoMapper {

    public UserResponseDTO toUserResponseDTO(User user) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getCreateDate()
        );
    }

    public User toUserEntity(UserRequestDTO userRequestDTO) {
        if (userRequestDTO == null) throw new IllegalArgumentException("User Request cannot be null");
        User user = new User();
        user.setEmail(userRequestDTO.email());
        user.setFullName(userRequestDTO.fullName());
        return user;
    }

    public UserProfileResponseDTO toUserProfileResponseDTO(UserProfile profile) {
        if (profile == null) return null;
        return new UserProfileResponseDTO(
                profile.getId(),
                profile.getNickname(),
                profile.getPhoneNumber(),
                profile.getBio(),
                profile.getAddress()
        );
    }

    public UserProfile toUserProfileEntity(UserProfileRequestDTO dto) {
        if (dto == null) return null;
        UserProfile profile = new UserProfile();
        profile.setNickname(dto.nickname());
        profile.setPhoneNumber(dto.phoneNumber());
        profile.setBio(dto.bio());
        profile.setAddress(dto.address());
        return profile;
    }


    public EventResponseDTO toEventResponseDTO(Event event) {
        if (event == null) throw new IllegalArgumentException("Event cannot be null");

        return new EventResponseDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDateTime(),
                event.getLocation(),
                event.getStatus().toString(),
                toUserResponseDTO(event.getCreatedBy()),
                event.getParticipants()
                        .stream()
                        .map(this::toUserResponseDTO)
                        .collect(Collectors.toSet())
        );
    }

    public Event toEventEntity(EventRequestDTO eventRequestDTO) {
        if (eventRequestDTO == null) throw new IllegalArgumentException("Event Request cannot be null");

        Event event = new Event();
        event.setTitle(eventRequestDTO.title());
        event.setDescription(eventRequestDTO.description());
        event.setDateTime(eventRequestDTO.dateTime());
        event.setLocation(eventRequestDTO.location());
        event.setStatus(EventStatus.fromString(eventRequestDTO.status()));
        return event;
    }
}
