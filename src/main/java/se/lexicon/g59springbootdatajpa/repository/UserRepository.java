package se.lexicon.g59springbootdatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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


    @Query("SELECT u FROM User u WHERE u.fullName LIKE %:name%")
    List<User> selectByFullNameContains(@Param("name") String fullName);


    @Query("SELECT u FROM User u WHERE u.createDate > :date")
    List<User> selectByCreateDateAfter(@Param("date") Instant createdDate);

    @Modifying
    @Query("UPDATE User u SET u.fullName = :name WHERE u.email = :email")
    int updateNameByEmail(@Param("name") String fullName, @Param("email") String email);
    // the return type can be int as a number of affected rows or make it to void

}
