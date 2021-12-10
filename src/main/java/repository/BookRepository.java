package repository;

import repository.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    List<Book> getAllBooks();
    Optional<Book> getBookByIsbn(String isbn);
    void saveBook(Book book);
    void updateBook(Book book);
    void deleteBook(Book book);
}
