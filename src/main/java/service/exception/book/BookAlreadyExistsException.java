package service.exception.book;

import service.exception.TupleAlreadyExistsException;

public class BookAlreadyExistsException extends TupleAlreadyExistsException {
    public BookAlreadyExistsException() {
        super();
    }

    public BookAlreadyExistsException(String isbn) {
        super(String.format("Book with isbn %s already exists.", isbn));
    }
}
