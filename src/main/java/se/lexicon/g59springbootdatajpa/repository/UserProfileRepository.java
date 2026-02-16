package se.lexicon.g59springbootdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.lexicon.g59springbootdatajpa.entity.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    // SAVE, DELETE, UPDATE, FINDByID, FINDALL
}
