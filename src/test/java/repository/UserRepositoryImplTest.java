package repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repository.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class UserRepositoryImplTest {
    @Mock
    EntityManager entityManager;
    @Mock
    Query query;
    @Mock
    TypedQuery<User> typedQuery;
    @InjectMocks
    UserRepositoryImpl userRepositoryImpl;

    static User user;

    @BeforeAll
    static void init() {
        user = createSampleUser();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetAllUsers() {
        when(entityManager.createQuery("select u from User u", User.class)).thenReturn(typedQuery);
        when(typedQuery.setHint("org.hibernate.cacheable", true)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.singletonList(user));

        List<User> expected = Collections.singletonList(user);
        List<User> result = userRepositoryImpl.getAllUsers();
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testGetUserByUsername() {
        when(entityManager.createNamedQuery("User.getByUsername")).thenReturn(query);
        when(query.setParameter("username", user.getUsername())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(user);

        Optional<User> expected = Optional.of(user);
        Optional<User> result = userRepositoryImpl.getUserByUsername(user.getUsername());
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testGetUserByEmail() {
        when(entityManager.createNamedQuery("User.getByEmail")).thenReturn(query);
        when(query.setParameter("email", user.getEmail())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(user);

        Optional<User> expected = Optional.of(user);
        Optional<User> result = userRepositoryImpl.getUserByEmail(user.getEmail());
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testSaveUser() {
        doNothing().when(entityManager).persist(user);
        userRepositoryImpl.saveUser(user);
    }

    @Test
    void testUpdateUser() {
        when(entityManager.merge(user)).thenReturn(user);
        userRepositoryImpl.updateUser(user);
    }

    @Test
    void testDeleteUser() {
        doNothing().when(entityManager).remove(user);
        userRepositoryImpl.deleteUser(user);
    }

    public static User createSampleUser() {
        return User.builder()
                .id(1L)
                .role(User.ROLE_USER)
                .email("test@test.com")
                .username("username")
                .password("password")
                .salt("salt")
                .build();
    }
}
