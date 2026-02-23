package se.lexicon.g59springbootdatajpa.dao;

import se.lexicon.g59springbootdatajpa.entity.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserDao {

    User save(User user);

    Optional<User> findById(Long id);

    List<User> findAll();

    void delete(User user);

    Optional<User> findByEmail(String email);

    List<User> findByFullNameContaining(String name);

    List<User> findByCreateDateAfter(Instant date);

    boolean existsByEmail(String email);

    int updateFullNameByEmail(String name, String email);
}
