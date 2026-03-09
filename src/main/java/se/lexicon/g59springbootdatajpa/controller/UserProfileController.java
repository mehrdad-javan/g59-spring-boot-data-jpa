package se.lexicon.g59springbootdatajpa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import se.lexicon.g59springbootdatajpa.dto.request.UserProfileRequestDTO;
import se.lexicon.g59springbootdatajpa.dto.response.UserProfileResponseDTO;
import se.lexicon.g59springbootdatajpa.service.UserProfileService;

@RestController
@RequestMapping("/api/v1/profiles")
@Validated
@Tag(name = "User Profile Controller", description = "APIs for managing user profiles")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Autowired
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find a profile by ID")
    public ResponseEntity<UserProfileResponseDTO> findById(@PathVariable @Positive Long id) {
        return userProfileService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a profile")
    public ResponseEntity<UserProfileResponseDTO> update(@PathVariable @Positive Long id, @Valid @RequestBody UserProfileRequestDTO profileRequestDto) {
        UserProfileResponseDTO updatedProfile = userProfileService.update(id, profileRequestDto);
        return ResponseEntity.ok(updatedProfile);
    }
}
