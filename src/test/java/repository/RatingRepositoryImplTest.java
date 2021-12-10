package repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repository.entity.Book;
import repository.entity.Rating;
import repository.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class RatingRepositoryImplTest {
    @Mock
    EntityManager entityManager;
    @Mock
    Query query;
    @Mock
    TypedQuery<Rating> typedQuery;
    @InjectMocks
    RatingRepositoryImpl ratingRepositoryImpl;

    static Book book;
    static User user;
    static Rating rating;

    @BeforeAll
    static void init() {
        book = BookRepositoryImplTest.createSampleBook();
        user = UserRepositoryImplTest.createSampleUser();
        rating = new Rating(1L, book, user, new BigDecimal("4.5"));
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetAllRatingsOfBook() {
        when(entityManager.createQuery("select r from Rating r where r.book = :book", Rating.class))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter("book", book)).thenReturn(typedQuery);
        when(typedQuery.setHint("org.hibernate.cacheable", true)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.singletonList(rating));

        List<Rating> expected = Collections.singletonList(rating);
        List<Rating> result = ratingRepositoryImpl.getAllRatingsOfBook(book);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testGetAllRatingsOfUser() {
        when(entityManager.createQuery("select r from Rating r where r.user = :user", Rating.class))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter("user", user)).thenReturn(typedQuery);
        when(typedQuery.setHint("org.hibernate.cacheable", true)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.singletonList(rating));

        List<Rating> expected = Collections.singletonList(rating);
        List<Rating> result = ratingRepositoryImpl.getAllRatingsOfUser(user);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testGetRatingByBookAndUser() {
        when(entityManager.createNamedQuery("Rating.getByBookAndUser")).thenReturn(query);
        when(query.setParameter("book", book)).thenReturn(query);
        when(query.setParameter("user", user)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(rating);

        Optional<Rating> expected = Optional.of(rating);
        Optional<Rating> result = ratingRepositoryImpl.getRatingByBookAndUser(book, user);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testSaveRating() {
        doNothing().when(entityManager).persist(rating);
        ratingRepositoryImpl.saveRating(rating);
    }

    @Test
    void testUpdateRating() {
        when(entityManager.merge(rating)).thenReturn(rating);
        ratingRepositoryImpl.updateRating(rating);
    }

    @Test
    void testDeleteRating() {
        doNothing().when(entityManager).remove(rating);
        ratingRepositoryImpl.deleteRating(rating);
    }
}
