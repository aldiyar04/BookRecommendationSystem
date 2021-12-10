package service;

import repository.entity.Book;
import repository.entity.User;
import service.exception.role.InvalidRoleException;
import service.exception.user.NoSuchUserException;
import service.exception.user.UserAlreadyExistsException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);
    void registerUser(String email, String username, String password)
            throws UserAlreadyExistsException;
    boolean validateUserPassword(String username, String password) throws NoSuchUserException;
    void updateUserRole(String username, String newRole) throws NoSuchUserException, InvalidRoleException;
    void updateUsername(String username, String newUsername) throws UserAlreadyExistsException, NoSuchUserException;
    void updateUserEmail(String username, String newEmail) throws UserAlreadyExistsException, NoSuchUserException;
    void updateUserPassword(String username, String newPassword) throws NoSuchUserException;
    void deleteUser(String username) throws NoSuchUserException;

    List<Book> getBookRecommendations(String username) throws NoSuchUserException;
}
