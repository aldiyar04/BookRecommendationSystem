package repository;

import repository.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAllUsers();
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);
    void saveUser(User user);
    void updateUser(User user);
    void deleteUser(User user);
}
