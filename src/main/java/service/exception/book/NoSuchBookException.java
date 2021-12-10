package service.exception.book;

import service.exception.NoSuchTupleException;

public class NoSuchBookException extends NoSuchTupleException {
    public NoSuchBookException() {
        super();
    }

    public NoSuchBookException(String isbn) {
        super(String.format("No book with isbn %s.", isbn));
    }
}
