package repository;

import repository.entity.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Stateless
public class UserRepositoryImpl implements UserRepository {
    @PersistenceContext(unitName = "default")
    private EntityManager entityManager;

    @Override
    public List<User> getAllUsers() {
        return entityManager.createQuery("select u from User u", User.class)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        User user = null;
        try {
            user = (User) entityManager.createNamedQuery("User.getByUsername")
                    .setParameter("username", username)
                    .getSingleResult();
        } catch(NoResultException ignored) {
        }
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        User user = null;
        try {
            user = (User) entityManager.createNamedQuery("User.getByEmail")
                    .setParameter("email", email)
                    .getSingleResult();
        } catch(NoResultException ignored) {
        }
        return Optional.ofNullable(user);
    }

    @Override
    public void saveUser(User user) {
        entityManager.persist(user);
    }

    @Override
    public void updateUser(User user) {
        entityManager.merge(user);
    }

    @Override
    public void deleteUser(User user) {
        entityManager.remove(user);
    }
}
