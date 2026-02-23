
package se.lexicon.g59springbootdatajpa.reposiroty;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import se.lexicon.g59springbootdatajpa.entity.User;
import se.lexicon.g59springbootdatajpa.entity.UserProfile;
import se.lexicon.g59springbootdatajpa.repository.UserProfileRepository;
import se.lexicon.g59springbootdatajpa.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserProfileRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Test
    @DisplayName("save() should persist profile linked to a user (One-to-One owner side)")
    void save_shouldPersistProfileWithUser() {

        // Arrange
        User userEntity = new User();
        userEntity.setEmail("eva@example.com");
        userEntity.setFullName("Eva Eriksson");
        User user = userRepository.save(userEntity);

        UserProfile profile = new UserProfile();
        profile.setNickname("eva123");
        profile.setAddress("Stockholm");
        profile.setBio("Hello!");
        profile.setUser(user);

        // Act
        UserProfile savedProfile = userProfileRepository.save(profile);

        // Assert
        assertThat(savedProfile.getId()).isNotNull();
        assertThat(savedProfile.getUser()).isNotNull();
        assertThat(savedProfile.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("findByNickname() should return profile when nickname exists")
    void findByNickname_shouldReturnProfile() {
        // Arrange
        User userEntity = new User();
        userEntity.setEmail("nick@example.com");
        userEntity.setFullName("Nick Name");
        User user = userRepository.save(userEntity);

        UserProfile profile = new UserProfile();
        profile.setNickname("nicko");
        profile.setUser(user);
        userProfileRepository.save(profile);

        // Act + Assert
        assertThat(userProfileRepository.findByNickname("nicko")).isPresent();
    }

    @Test
    @DisplayName("findByAddressContaining() should return profiles matching address keyword")
    void findByAddressContaining_shouldWork() {
        // Arrange
        User u1Entity = new User();
        u1Entity.setEmail("a1@example.com");
        u1Entity.setFullName("A One");
        User u1 = userRepository.save(u1Entity);

        User u2Entity = new User();
        u2Entity.setEmail("a2@example.com");
        u2Entity.setFullName("A Two");
        User u2 = userRepository.save(u2Entity);

        UserProfile p1 = new UserProfile();
        p1.setNickname("p1");
        p1.setAddress("Stockholm, Sweden");
        p1.setUser(u1);
        userProfileRepository.save(p1);

        UserProfile p2 = new UserProfile();
        p2.setNickname("p2");
        p2.setAddress("Gothenburg, Sweden");
        p2.setUser(u2);
        userProfileRepository.save(p2);

        // Act
        List<UserProfile> result = userProfileRepository.findByAddressContaining("Sweden");

        // Assert
        assertThat(result).hasSize(2);
    }
}
