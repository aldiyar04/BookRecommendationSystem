package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repository.UserRepository;
import repository.UserRepositoryImplTest;
import repository.entity.User;
import service.exception.role.InvalidRoleException;
import service.exception.user.NoSuchUserException;
import service.exception.user.UserAlreadyExistsException;
import service.util.PasswordUtil;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class UserServiceImplTest {
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordUtil passwordUtil;
    @InjectMocks
    UserServiceImpl userServiceImpl;

    static User user;
    static List<User> userList;

    @BeforeAll
    static void init() {
        user = UserRepositoryImplTest.createSampleUser();
        userList = Collections.singletonList(user);
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(userRepository.getUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.getAllUsers()).thenReturn(userList);

        List<User> expected = userList;
        List<User> result = userServiceImpl.getAllUsers();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testGetUserByUsername() {
        Optional<User> expected = Optional.of(user);
        Optional<User> result = userServiceImpl.getUserByUsername(user.getUsername());
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testGetUserByEmail() {
        when(userRepository.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Optional<User> expected = Optional.of(user);
        Optional<User> result = userServiceImpl.getUserByEmail(user.getEmail());
        Assertions.assertEquals(expected, result);
    }

    @Test
    void registerUserShouldThrowException() {
        when(userRepository.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));

        try {
            userServiceImpl.registerUser(user.getEmail(), user.getUsername(), user.getPassword());
        } catch (UserAlreadyExistsException success) {
            return;
        }

        Assertions.fail("Should throw UserAlreadyExistsException.");
    }

    @Test
    void registerUserShouldNotThrowException() {
        String username = "UniqueUsername";
        String email = "UniqueEmail";

        when(userRepository.getUserByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.getUserByEmail(email)).thenReturn(Optional.empty());

        try {
            userServiceImpl.registerUser(email, username, "password");
        } catch (UserAlreadyExistsException e) {
            Assertions.fail(String.format("Should not throw %s.", e.getClass()));
        }
    }

    @Test
    void validateUserPasswordShouldReturnTrue() {
        when(passwordUtil.getEncryptedPassword(user.getPassword(), user.getSalt())).thenReturn(user.getPassword());

        boolean result = false;
        try {
            result = userServiceImpl.validateUserPassword(user.getUsername(), user.getPassword());
        } catch (NoSuchUserException e) {
            Assertions.fail(String.format("Should not throw %s.", e.getClass()));
        }
        Assertions.assertTrue(result);
    }

    @Test
    void validateUserPasswordShouldReturnFalse() {
        String invalidPassword = "invalid password";
        when(passwordUtil.getEncryptedPassword(invalidPassword, user.getSalt())).thenReturn(user.getPassword());

        boolean result = false;
        try {
            result = userServiceImpl.validateUserPassword(user.getUsername(), invalidPassword);
        } catch (NoSuchUserException e) {
            Assertions.fail(String.format("Should not throw %s.", e.getClass()));
        }
        Assertions.assertTrue(result);
    }

    @Test
    void validateUserPasswordShouldThrowException() {
        String notExistingUsername = "not existing username";

        when(passwordUtil.getEncryptedPassword(notExistingUsername, user.getSalt())).thenReturn(user.getPassword());

        boolean result = false;
        try {
            result = userServiceImpl.validateUserPassword(notExistingUsername,user.getPassword());
        } catch (NoSuchUserException success) {
            return;
        }
        Assertions.fail("Should throw NoSuchUserException");
    }

    @Test
    void testUpdateUserPassword() {
        doNothing().when(userRepository).updateUser(user);
        try {
            userServiceImpl.updateUserPassword(user.getUsername(), "newPassword");
        } catch (NoSuchUserException e) {
            Assertions.fail(String.format("Should not throw %s.", e.getClass()));
        }
    }

    @Test
    void testUpdateRole() {
        doNothing().when(userRepository).updateUser(user);
        try {
            userServiceImpl.updateUserRole(user.getUsername(), "NewRole");
        } catch(InvalidRoleException success) {
            return;
        } catch (NoSuchUserException e) {
            Assertions.fail(String.format("Should not throw %s.", e.getClass()));
        }
        Assertions.fail(String.format("Should throw %s.", InvalidRoleException.class));
    }

    @Test
    void testUpdateUsername() {
        doNothing().when(userRepository).updateUser(user);
        try {
            userServiceImpl.updateUsername(user.getUsername(), "NewUsername");
        } catch (NoSuchUserException | UserAlreadyExistsException e) {
            Assertions.fail(String.format("Should not throw %s.", e.getClass()));
        }
    }

    @Test
    void testUpdateUserEmail() {
        doNothing().when(userRepository).updateUser(user);
        try {
            userServiceImpl.updateUserEmail(user.getUsername(), "newemail@test.com");
        } catch (NoSuchUserException | UserAlreadyExistsException e) {
            Assertions.fail(String.format("Should not throw %s.", e.getClass()));
        }
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).deleteUser(user);

        try {
            userServiceImpl.deleteUser(user.getUsername());
        } catch (NoSuchUserException e) {
            Assertions.fail(String.format("Should not throw %s.", e.getClass()));
        }
    }
}
