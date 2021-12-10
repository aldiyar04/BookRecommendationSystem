package repository.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repository.entity.Author;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class AuthorDaoImplTest {
    @Mock
    EntityManager entityManager;
    @Mock
    Query query;
    @InjectMocks
    AuthorDaoImpl authorDaoImpl;

    static Author author;

    @BeforeAll
    static void init() {
        author = new Author(1L, "name", new ArrayList<>());
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetAuthorByName() {
        when(entityManager.createNamedQuery("Author.getByName")).thenReturn(query);
        when(query.setParameter("name", author.getName())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(author);

        Optional<Author> expected = Optional.of(author);
        Optional<Author> result = authorDaoImpl.getAuthorByName(author.getName());
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testSaveAuthor() {
        doNothing().when(entityManager).persist(author);
        authorDaoImpl.saveAuthor(author);
    }

    @Test
    void testUpdateAuthor() {
        when(entityManager.merge(author)).thenReturn(author);
        authorDaoImpl.updateAuthor(author);
    }

    @Test
    void testDeleteAuthor() {
        doNothing().when(entityManager).remove(author);
        authorDaoImpl.deleteAuthor(author);
    }
}
