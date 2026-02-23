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

    /* https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
     * Derived Query Methods (Query Creation from Method Names)
     * -------------------------------------------------------
     * Spring Data JPA can "parse" the method name and automatically
     * generate the SQL query for you.
     * <p>
     * Rules:
     * - Start with 'findBy', 'getBy', 'readBy', 'queryBy', or 'countBy'.
     * - Append the property name (e.g., Email).
     * - Add keywords like 'Containing', 'After', 'Between', etc.
     */

    // SELECT * FROM user WHERE email = ?
    Optional<User> findByEmail(String email);

    // SELECT * FROM user WHERE full_name LIKE %?%
    List<User> findByFullNameContaining(String name);

    // SELECT * FROM user WHERE create_date > ?
    List<User> findByCreateDateAfter(Instant date);

    // SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM user WHERE email = ?
    boolean existsByEmail(String email);


    // SELECT * FROM user WHERE full_name LIKE %?%
    @Query("SELECT u FROM User u WHERE u.fullName LIKE %:name%")
    List<User> selectByFullNameContaining(@Param("name") String name);

    // SELECT * FROM user WHERE create_date > ?
    @Query("SELECT u FROM User u WHERE u.createDate > :date")
    List<User> selectByCreateDateAfter(@Param("date") Instant date);

    @Modifying
    @Query("UPDATE User u SET u.fullName = :name WHERE u.email = :email")
    int updateFullNameByEmail(@Param("name") String name, @Param("email") String email);


}
