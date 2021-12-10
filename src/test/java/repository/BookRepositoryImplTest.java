package repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repository.entity.Author;
import repository.entity.Book;
import repository.entity.Genre;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class BookRepositoryImplTest {
    @Mock
    EntityManager entityManager;
    @Mock
    Query query;
    @Mock
    TypedQuery<Book> typedQuery;
    @InjectMocks
    BookRepositoryImpl bookRepositoryImpl;

    static Book book;

    @BeforeAll
    static void init() {
        book = createSampleBook();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetAllBooks() {
        when(entityManager.createQuery("select b from Book b", Book.class)).thenReturn(typedQuery);
        when(typedQuery.setHint("org.hibernate.cacheable", true)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.singletonList(book));

        List<Book> expected = Collections.singletonList(book);
        List<Book> result = bookRepositoryImpl.getAllBooks();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testGetBookByIsbn() {
        when(entityManager.createNamedQuery("Book.getByIsbn")).thenReturn(query);
        when(query.setParameter("isbn", book.getIsbn())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(book);

        Optional<Book> expected = Optional.of(book);
        Optional<Book> result = bookRepositoryImpl.getBookByIsbn("isbn");
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testSaveBook() {
        doNothing().when(entityManager).persist(book);
        bookRepositoryImpl.saveBook(book);
    }

    @Test
    void testUpdateBook() {
        when(entityManager.merge(book)).thenReturn(book);
        bookRepositoryImpl.updateBook(book);
    }

    @Test
    void testDeleteBook() {
        doNothing().when(entityManager).remove(book);
        bookRepositoryImpl.deleteBook(book);
    }

    public static Book createSampleBook() {
        Author author = new Author(1L, "author", new ArrayList<>());
        Genre genre = new Genre(1L, "genre", new ArrayList<>());
        book = Book.builder()
                .id(1L)
                .title("title")
                .description("description")
                .isbn("isbn")
                .yearPublished((short) 2000)
                .authors(Collections.singletonList(author))
                .genres(Collections.singletonList(genre))
                .build();
        author.getBooks().add(book);
        genre.getBooks().add(book);
        return book;
    }
}
