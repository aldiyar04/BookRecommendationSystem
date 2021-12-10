package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repository.*;
import repository.entity.Book;
import repository.entity.Rating;
import repository.entity.User;
import service.exception.book.BookAlreadyExistsException;
import service.exception.book.NoSuchBookException;
import service.exception.rating.NoSuchRatingException;
import service.exception.user.NoSuchUserException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class BookServiceImplTest {
    @Mock
    BookRepository bookRepository;
    @Mock
    RatingRepository ratingRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    BookServiceImpl bookServiceImpl;

    static Book book;
    static User user;
    static Rating rating;

    @BeforeAll
    static void init() {
        book = BookRepositoryImplTest.createSampleBook();
        user = UserRepositoryImplTest.createSampleUser();
        rating = new Rating(1l, book, user, new BigDecimal("4.0"));
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetAllBooks() {
        when(bookRepository.getAllBooks()).thenReturn(Collections.singletonList(book));

        List<Book> expected = Collections.singletonList(book);
        List<Book> result = bookServiceImpl.getAllBooks();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testGetBookByIsbn() {
        when(bookRepository.getBookByIsbn(book.getIsbn())).thenReturn(Optional.of(book));

        Optional<Book> expected = Optional.of(book);
        Optional<Book> result = bookServiceImpl.getBookByIsbn(book.getIsbn());
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testAddBook() {
        when(bookRepository.getBookByIsbn(book.getIsbn())).thenReturn(Optional.empty());
        doNothing().when(bookRepository).saveBook(book);
        try {
            bookServiceImpl.addBook(book);
        } catch (BookAlreadyExistsException e) {
            Assertions.fail(String.format("Should not throw %s.", e.getClass()));
        }
    }

    @Test
    void testUpdateBookShouldNotThrowException() {
        Book replacingBook = createSampleReplacingBook();

        when(bookRepository.getBookByIsbn(book.getIsbn())).thenReturn(Optional.of(book));
        when(bookRepository.getBookByIsbn(replacingBook.getIsbn())).thenReturn(Optional.empty());

        try {
            bookServiceImpl.updateBook(book.getIsbn(), replacingBook);
        } catch (NoSuchBookException | BookAlreadyExistsException e) {
            Assertions.fail(String.format("Should not throw %s.", e.getClass()));
        }
    }

    @Test
    void testUpdateBookShouldThrowNoSuchBookException() {
        Book replacingBook = createSampleReplacingBook();

        when(bookRepository.getBookByIsbn(book.getIsbn())).thenReturn(Optional.empty());
        when(bookRepository.getBookByIsbn(replacingBook.getIsbn())).thenReturn(Optional.empty());

        try {
            bookServiceImpl.updateBook(book.getIsbn(), replacingBook);
        } catch (NoSuchBookException success) {
            return;
        } catch (BookAlreadyExistsException e) {
            Assertions.fail(String.format("Should not throw %s.", e.getClass()));
        }
        Assertions.fail("Should throw NoSuchBookException.");
    }

    @Test
    void testUpdateBookShouldThrowBookAlreadyExistsException() {
        Book replacingBook = createSampleReplacingBook();

        when(bookRepository.getBookByIsbn(book.getIsbn())).thenReturn(Optional.of(book));
        when(bookRepository.getBookByIsbn(replacingBook.getIsbn())).thenReturn(
                Optional.of(createSampleReplacingBook())
        );

        try {
            bookServiceImpl.updateBook(book.getIsbn(), replacingBook);
        } catch (BookAlreadyExistsException success) {
            return;
        } catch (NoSuchBookException e) {
            Assertions.fail(String.format("Should not throw %s.", e.getClass()));
        }
        Assertions.fail("Should throw BookAlreadyExistsException.");
    }

    @Test
    void testDeleteBook() {
        when(bookRepository.getBookByIsbn(book.getIsbn())).thenReturn(Optional.of(book));
        try {
            bookServiceImpl.deleteBook(book.getIsbn());
        } catch (NoSuchBookException e) {
            Assertions.fail(String.format("Should not throw %s.", e.getClass()));
        }
    }

    @Test
    void testRateBookSave() {
        when(bookRepository.getBookByIsbn(book.getIsbn())).thenReturn(Optional.of(book));
        when(userRepository.getUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(ratingRepository.getRatingByBookAndUser(book, user)).thenReturn(Optional.empty());

        doNothing().when(ratingRepository).saveRating(rating);
        try {
            bookServiceImpl.rateBook(book.getIsbn(), user.getUsername(), rating.getValue());
        } catch (NoSuchBookException | NoSuchUserException e) {
            Assertions.fail(String.format("Should not throw %s.", e.getClass()));
        }
    }

    @Test
    void testRateBookUpdate() {
        when(bookRepository.getBookByIsbn(book.getIsbn())).thenReturn(Optional.of(book));
        when(userRepository.getUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(ratingRepository.getRatingByBookAndUser(book, user)).thenReturn(Optional.of(rating));
        doNothing().when(ratingRepository).updateRating(rating);

        try {
            bookServiceImpl.rateBook(book.getIsbn(), user.getUsername(), rating.getValue());
        } catch (NoSuchUserException | NoSuchBookException e) {
            Assertions.fail(String.format("Should not throw %s.", e.getClass()));
        }
    }

    @Test
    void testUnrateBookShouldNotThrowException() {
        when(bookRepository.getBookByIsbn(book.getIsbn())).thenReturn(Optional.of(book));
        when(userRepository.getUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(ratingRepository.getRatingByBookAndUser(book, user)).thenReturn(Optional.of(rating));
        doNothing().when(ratingRepository).deleteRating(rating);

        try {
            bookServiceImpl.unrateBook(book.getIsbn(), user.getUsername());
        } catch (NoSuchBookException | NoSuchUserException | NoSuchRatingException e) {
            Assertions.fail(String.format("Should not throw %s.", e.getClass()));
        }
    }

    @Test
    void testUnrateBookShouldThrowNoSuchRatingException() {
        when(bookRepository.getBookByIsbn(book.getIsbn())).thenReturn(Optional.of(book));
        when(userRepository.getUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(ratingRepository.getRatingByBookAndUser(book, user)).thenReturn(Optional.empty());
        doNothing().when(ratingRepository).deleteRating(rating);

        try {
            bookServiceImpl.unrateBook(book.getIsbn(), user.getUsername());
        } catch (NoSuchRatingException success) {
            return;
        } catch (NoSuchBookException | NoSuchUserException e) {
            Assertions.fail(String.format("Should not throw %s.", e.getClass()));
        }
        Assertions.fail(String.format("Should throw %s", NoSuchRatingException.class));
    }



    Book createSampleReplacingBook() {
        return Book.builder()
                .title("new title")
                .description("new description")
                .isbn("new isbn")
                .yearPublished((short) 1950)
                .build();
    }
}
