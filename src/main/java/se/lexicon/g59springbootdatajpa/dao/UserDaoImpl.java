package se.lexicon.g59springbootdatajpa.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import se.lexicon.g59springbootdatajpa.entity.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public User save(User user) {
        if (user.getId() == null) {
            entityManager.persist(user);
            return user;
        } else {
            return entityManager.merge(user);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    @Override
    public List<User> findAll() {
        return entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    @Override
    @Transactional
    public void delete(User user) {
        if (entityManager.contains(user)) {
            entityManager.remove(user);
        } else {
            User existingUser = entityManager.find(User.class, user.getId());
            if (existingUser != null) {
                entityManager.remove(existingUser);
            }
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<User> findByFullNameContaining(String name) {
        return entityManager.createQuery("SELECT u FROM User u WHERE u.fullName LIKE :name", User.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }

    @Override
    public List<User> findByCreateDateAfter(Instant date) {
        return entityManager.createQuery("SELECT u FROM User u WHERE u.createDate > :date", User.class)
                .setParameter("date", date)
                .getResultList();
    }

    @Override
    public boolean existsByEmail(String email) {
        Long count = entityManager.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional
    public int updateFullNameByEmail(String name, String email) {
        return entityManager.createQuery("UPDATE User u SET u.fullName = :name WHERE u.email = :email")
                .setParameter("name", name)
                .setParameter("email", email)
                .executeUpdate();
    }
}
