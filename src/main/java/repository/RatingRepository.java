package repository;

import repository.entity.Book;
import repository.entity.Rating;
import repository.entity.User;

import java.util.List;
import java.util.Optional;

public interface RatingRepository {
    List<Rating> getAllRatingsOfBook(Book book);
    List<Rating> getAllRatingsOfUser(User user);
    Optional<Rating> getRatingByBookAndUser(Book book, User user);
    void saveRating(Rating rating);
    void updateRating(Rating rating);
    void deleteRating(Rating rating);
}
