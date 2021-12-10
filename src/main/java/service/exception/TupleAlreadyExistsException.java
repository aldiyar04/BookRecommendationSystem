package service.exception;

public class TupleAlreadyExistsException extends TupleException {
    public TupleAlreadyExistsException() {
        super();
    }

    public TupleAlreadyExistsException(String message) {
        super(message);
    }
}
