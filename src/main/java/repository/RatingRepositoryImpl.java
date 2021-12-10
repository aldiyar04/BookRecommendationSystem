package repository;

import repository.entity.Book;
import repository.entity.Rating;
import repository.entity.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Stateless
public class RatingRepositoryImpl implements RatingRepository {
    @PersistenceContext(unitName = "default")
    private EntityManager entityManager;

    @Override
    public List<Rating> getAllRatingsOfBook(Book book) {
        return entityManager.createQuery("select r from Rating r where r.book = :book", Rating.class)
                .setParameter("book", book)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }

    @Override
    public List<Rating> getAllRatingsOfUser(User user) {
        return entityManager.createQuery("select r from Rating r where r.user = :user", Rating.class)
                .setParameter("user", user)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }

    @Override
    public Optional<Rating> getRatingByBookAndUser(Book book, User user) {
        Rating rating = null;
        try {
            rating = (Rating) entityManager.createNamedQuery("Rating.getByBookAndUser")
                    .setParameter("book", book)
                    .setParameter("user", user)
                    .getSingleResult();
        } catch(NoResultException ignored) {
        }
        return Optional.ofNullable(rating);
    }

    @Override
    public void saveRating(Rating rating) {
        entityManager.persist(rating);
    }

    @Override
    public void updateRating(Rating rating) {
        entityManager.merge(rating);
    }

    @Override
    public void deleteRating(Rating rating) {
        entityManager.remove(rating);
    }
}
