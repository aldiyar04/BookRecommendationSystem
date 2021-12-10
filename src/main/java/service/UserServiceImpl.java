package service;

import repository.UserRepository;
import repository.entity.Book;
import repository.entity.User;
import service.exception.role.InvalidRoleException;
import service.exception.user.NoSuchUserException;
import service.exception.user.UserAlreadyExistsException;
import service.exception.user.UserField;
import service.util.PasswordUtil;
import service.util.RecommendationUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.util.*;

@Stateless
public class UserServiceImpl implements UserService {
    @EJB
    private UserRepository userRepository;
    @EJB
    private PasswordUtil passwordUtil;
    @EJB
    private RecommendationUtil recommendationUtil;

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }


    @Override
    public void registerUser(String email, String username, String password) throws UserAlreadyExistsException {
        if(isUsernameTaken(username))
            throw new UserAlreadyExistsException(UserField.USERNAME, username);
        if(isEmailTaken(email))
            throw new UserAlreadyExistsException(UserField.EMAIL, email);

        String salt = passwordUtil.getNewSalt();
        String encryptedPassword = passwordUtil.getEncryptedPassword(password, salt);

        final User user = User.builder()
                .role(User.ROLE_USER)
                .email(email)
                .username(username)
                .password(encryptedPassword)
                .salt(salt)
                .build();
        userRepository.saveUser(user);
    }

    @Override
    public boolean validateUserPassword(String username, String password) throws NoSuchUserException {
        User user = getUser(username);
        String salt = user.getSalt();
        String validPassword = user.getPassword();
        String passedPassword = passwordUtil.getEncryptedPassword(password, salt);
        return passedPassword.equals(validPassword);
    }

    @Override
    public void updateUserRole(String username, String newRole) throws NoSuchUserException, InvalidRoleException {
        User user = getUser(username);

        if(!Objects.equals(newRole, User.ROLE_USER) &&
                !Objects.equals(newRole, User.ROLE_LIBRARIAN) &&
                !Objects.equals(newRole, User.ROLE_ADMIN)) {

            throw new InvalidRoleException(String.format("No role \"%s\". Valid roles: %s, %s, %s.", newRole,
                    User.ROLE_USER, User.ROLE_LIBRARIAN, User.ROLE_ADMIN));
        }

        user.setRole(newRole);
        userRepository.updateUser(user);
    }

    @Override
    public void updateUsername(String username, String newUsername)
            throws UserAlreadyExistsException, NoSuchUserException {

        User user = getUser(username);
        if(isUsernameTaken(newUsername))
            throw new UserAlreadyExistsException(UserField.USERNAME, newUsername);

        user.setUsername(newUsername);
        userRepository.updateUser(user);
    }

    @Override
    public void updateUserEmail(String username, String newEmail)
            throws UserAlreadyExistsException, NoSuchUserException {

        User user = getUser(username);
        if(isEmailTaken(newEmail))
            throw new UserAlreadyExistsException(UserField.EMAIL, newEmail);

        user.setEmail(newEmail);
        userRepository.updateUser(user);
    }

    @Override
    public void updateUserPassword(String username, String newPassword) throws NoSuchUserException {
        User user = getUser(username);
        String newSalt = passwordUtil.getNewSalt();
        String newPasswordEncrypted = passwordUtil.getEncryptedPassword(newPassword, newSalt);
        user.setPassword(newPasswordEncrypted);
        userRepository.updateUser(user);
    }

    @Override
    public void deleteUser(String username) throws NoSuchUserException {
        userRepository.deleteUser(getUser(username));
    }

    @Override
    public List<Book> getBookRecommendations(String username) throws NoSuchUserException {
        Map<Book, BigDecimal> recommendations = recommendationUtil.getBookRecommendations(getUser(username));
        return new ArrayList<>(recommendations.keySet());
    }



    private boolean isUsernameTaken(String username) {
        return userRepository.getUserByUsername(username).isPresent();
    }

    private boolean isEmailTaken(String email) {
        return userRepository.getUserByEmail(email).isPresent();
    }

    private User getUser(String username) throws NoSuchUserException {
        Optional<User> userOptional = userRepository.getUserByUsername(username);
        if(!userOptional.isPresent())
            throw new NoSuchUserException(username);
        return userOptional.get();
    }
}
