package service.exception.rating;

import service.exception.NoSuchTupleException;

public class NoSuchRatingException extends NoSuchTupleException {
    public NoSuchRatingException() {
        super();
    }

    public NoSuchRatingException(String message) {
        super(message);
    }

    public NoSuchRatingException(String isbn, String username) {
        this(String.format("Rating of book with isbn %s by user %s does not exist.", isbn, username));
    }
}
