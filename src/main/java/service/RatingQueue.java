package service;

import repository.entity.Rating;

import java.util.Optional;

public interface RatingQueue {
    void sendRating(Rating rating);
    Optional<Rating> retrieveNextRating();
}
