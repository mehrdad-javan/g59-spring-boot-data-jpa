package se.lexicon.g59springbootdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.lexicon.g59springbootdatajpa.entity.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html

    // select * from users where email = '?'
    Optional<User> findByEmail(String email);

    // select * from users where fullName like %?%
    List<User> findByFullNameContains(String fullName);

    // seelct * from users where createDate > ?
    List<User> findByCreateDateAfter(Instant createdDate);

    // select count(*) from users where email = ?
    boolean existsByEmail(String email);

}
