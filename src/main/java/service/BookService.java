package service;

import repository.entity.Book;
import service.exception.book.BookAlreadyExistsException;
import service.exception.book.NoSuchBookException;
import service.exception.rating.NoSuchRatingException;
import service.exception.user.NoSuchUserException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BookService {
    List<Book> getAllBooks();
    Optional<Book> getBookByIsbn(String isbn);
    void addBook(Book book) throws BookAlreadyExistsException;
    void updateBook(String isbn, Book replacingBook) throws NoSuchBookException, BookAlreadyExistsException;
    void deleteBook(String isbn) throws NoSuchBookException;

    void rateBook(String isbn, String username, BigDecimal value) throws NoSuchBookException, NoSuchUserException;
    void unrateBook(String isbn, String username) throws NoSuchBookException, NoSuchUserException, NoSuchRatingException;
}
