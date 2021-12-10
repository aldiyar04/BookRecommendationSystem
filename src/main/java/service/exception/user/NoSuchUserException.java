package service.exception.user;

import service.exception.NoSuchTupleException;

public class NoSuchUserException extends NoSuchTupleException {
    public NoSuchUserException() {
        super();
    }

    public NoSuchUserException(String username) {
        super(String.format("User %s does not exist.", username));
    }
}
