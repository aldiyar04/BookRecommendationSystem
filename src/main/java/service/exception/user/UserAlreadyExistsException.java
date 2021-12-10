package service.exception.user;

import service.exception.TupleAlreadyExistsException;

public class UserAlreadyExistsException extends TupleAlreadyExistsException {
    public UserAlreadyExistsException() {
        super();
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(UserField userField, String userFieldValue) {
        this(userField == UserField.USERNAME ?
                String.format("Username %s is already taken.", userFieldValue) :
                String.format("Email %s is already taken.", userFieldValue)
        );
    }
}
