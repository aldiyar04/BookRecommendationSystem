package repository.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repository.entity.Genre;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class GenreDaoImplTest {
    @Mock
    EntityManager entityManager;
    @Mock
    Query query;
    @InjectMocks
    GenreDaoImpl genreDaoImpl;

    static Genre genre;

    @BeforeAll
    static void init() {
        genre = new Genre(1L, "name", new ArrayList<>());
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetGenreByName() {
        when(entityManager.createNamedQuery("Genre.getByName")).thenReturn(query);
        when(query.setParameter("name", genre.getName())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(genre);

        Optional<Genre> expected = Optional.of(genre);
        Optional<Genre> result = genreDaoImpl.getGenreByName(genre.getName());
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testSaveGenre() {
        doNothing().when(entityManager).persist(genre);
        genreDaoImpl.saveGenre(genre);
    }

    @Test
    void testUpdateGenre() {
        when(entityManager.merge(genre)).thenReturn(genre);
        genreDaoImpl.updateGenre(genre);
    }

    @Test
    void testDeleteGenre() {
        doNothing().when(entityManager).remove(genre);
        genreDaoImpl.deleteGenre(genre);
    }
}