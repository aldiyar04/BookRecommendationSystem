package service.exception.role;

public class InvalidRoleException extends Exception {
    public InvalidRoleException() {
        super();
    }

    public InvalidRoleException(String message) {
        super(message);
    }
}
