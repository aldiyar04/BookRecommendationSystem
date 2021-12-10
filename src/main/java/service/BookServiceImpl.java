package service;

import repository.BookRepository;
import repository.RatingRepository;
import repository.UserRepository;
import repository.entity.*;
import service.exception.book.BookAlreadyExistsException;
import service.exception.book.NoSuchBookException;
import service.exception.rating.NoSuchRatingException;
import service.exception.user.NoSuchUserException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Stateless
public class BookServiceImpl implements BookService {
    @EJB
    private BookRepository bookRepository;
    @EJB
    private RatingRepository ratingRepository;
    @Inject
    private RatingQueue ratingQueue;

    @EJB
    private UserRepository userRepository;

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.getAllBooks();
    }

    @Override
    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.getBookByIsbn(isbn);
    }

    @Override
    public void addBook(Book book) throws BookAlreadyExistsException {
        if(isIsbnTaken(book.getIsbn())) {
            throw new BookAlreadyExistsException(book.getIsbn());
        }
        bookRepository.saveBook(book);
    }

    @Override
    public void updateBook(String isbn, Book replacingBook) throws NoSuchBookException, BookAlreadyExistsException {
        String newTitle = replacingBook.getTitle();
        String newDescription = replacingBook.getDescription();
        String newIsbn = replacingBook.getIsbn();
        Short newYearPublished = replacingBook.getYearPublished();
        List<Author> newAuthors = replacingBook.getAuthors();
        List<Genre> newGenres = replacingBook.getGenres();

        if(!newIsbn.equals(isbn) && isIsbnTaken(newIsbn))
            throw new BookAlreadyExistsException(isbn);

        Book book = getBook(isbn);
        book.setTitle(newTitle);
        book.setDescription(newDescription);
        book.setIsbn(newIsbn);
        book.setYearPublished(newYearPublished);
        book.setAuthors(newAuthors);
        book.setGenres(newGenres);

        bookRepository.updateBook(book);
    }

    @Override
    public void deleteBook(String isbn) throws NoSuchBookException {
        bookRepository.deleteBook(getBook(isbn));
    }

    @Override
    public void rateBook(String isbn, String username, BigDecimal value) throws NoSuchBookException, NoSuchUserException {
        Book book = getBook(isbn);
        User user = getUser(username);
        Rating rating = Rating.builder()
                .book(book)
                .user(user)
                .value(value)
                .build();

        Optional<Rating> ratingOptional = ratingRepository.getRatingByBookAndUser(book, user);

        if(ratingOptional.isPresent())
            ratingRepository.updateRating(rating);
        else {
            ratingRepository.saveRating(rating);
            ratingQueue.sendRating(Rating.builder().value(rating.getValue()).build());
        }
    }

    @Override
    public void unrateBook(String isbn, String username)
            throws NoSuchBookException, NoSuchUserException, NoSuchRatingException {

        Book book = getBook(isbn);
        User user = getUser(username);

        Optional<Rating> ratingOptional = ratingRepository.getRatingByBookAndUser(book, user);
        if(!ratingOptional.isPresent())
            throw new NoSuchRatingException(isbn, username);

        ratingQueue.retrieveNextRating();
        ratingRepository.deleteRating(ratingOptional.get());
    }

    private boolean isIsbnTaken(String isbn) {
        return bookRepository.getBookByIsbn(isbn).isPresent();
    }

    private Book getBook(String isbn) throws NoSuchBookException {
        Optional<Book> bookOptional = bookRepository.getBookByIsbn(isbn);
        if(!bookOptional.isPresent())
            throw new NoSuchBookException(isbn);
        return bookOptional.get();
    }

    private User getUser(String username) throws NoSuchUserException {
        Optional<User> userOptional = userRepository.getUserByUsername(username);
        if(!userOptional.isPresent())
            throw new NoSuchUserException(username);
        return userOptional.get();
    }
}
