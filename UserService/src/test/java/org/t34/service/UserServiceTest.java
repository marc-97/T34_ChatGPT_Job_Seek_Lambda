package org.t34.service;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.t34.dao.UserDAO;
import org.t34.dto.LoginDTO;
import org.t34.dto.UserContextDTO;
import org.t34.entity.User;
import org.t34.exception.InvalidPasswordException;
import org.t34.exception.NotFoundException;
import org.t34.util.GeneralHelper;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserDAO userDAO;
    @Mock
    private SessionFactory sessionFactory;
    private MockedStatic<GeneralHelper> generalHelper;

    private UserService userService;
    private AutoCloseable closeable;


    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        closeable = MockitoAnnotations.openMocks(this);
        userService = new UserService(sessionFactory);
        Field userDAOFIeld = userService.getClass().getDeclaredField("userDAO");
        userDAOFIeld.setAccessible(true);
        userDAOFIeld.set(userService, userDAO);
        generalHelper = mockStatic(GeneralHelper.class);
        generalHelper.when(() -> GeneralHelper.isPasswordMatch(any(), any())).thenReturn(true);
        generalHelper.when(() -> GeneralHelper.encodeJWT(any())).thenReturn("jwt");
    }

    @AfterEach
    public void releaseMocks() throws Exception {
        closeable.close();
        generalHelper.close();
    }

    @Test
    public void testLoginValidUser() throws NotFoundException, InvalidPasswordException {
        LoginDTO loginDTO = new LoginDTO("user@example.com", "password");
        User user = new User(1L, "user@example.com", "hashedPassword");
        when(userDAO.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));

        UserContextDTO userContextDTO = userService.login(loginDTO);

        assertNotNull(userContextDTO);
        assertEquals("jwt", userContextDTO.getJwtToken());
    }

    @Test
    public void testLoginUserNotFound() {
        LoginDTO loginDTO = new LoginDTO("nonexistent@example.com", "password");
        when(userDAO.findByEmail(loginDTO.getEmail())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> userService.login(loginDTO));
        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    public void testLoginInvalidPassword() {
        LoginDTO loginDTO = new LoginDTO("user@example.com", "wrongPassword");
        User user = new User(1L, "user@example.com", "hashedPassword");
        when(userDAO.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));
        generalHelper.when(() -> GeneralHelper.isPasswordMatch(any(), any())).thenReturn(false);

        Exception exception = assertThrows(InvalidPasswordException.class, () -> userService.login(loginDTO));
        assertTrue(exception.getMessage().contains("Invalid password"));
    }

    @Test
    public void testCreateUser() {
        User user = new User(1L, "user@example.com", "password");
        String result = userService.create(user);

        assertEquals("OK", result);
    }

    @Test
    public void testUpdateUser() throws NotFoundException {
        int userId = 1;
        User updatedUser = new User(1L, "updated@example.com", "newPassword");
        User existingUser = new User(1L, "user@example.com", "password");
        when(userDAO.findById(userId)).thenReturn(Optional.of(existingUser));

        String result = userService.update(updatedUser, userId);

        assertEquals("OK", result);
    }

    @Test
    public void testUpdateUserNotFound() {
        int userId = 1;
        User updatedUser = new User(1L, "updated@example.com", "newPassword");
        when(userDAO.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> userService.update(updatedUser, userId));
        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    public void testViewUser() throws NotFoundException {
        int userId = 1;
        User user = new User(1L, "user@example.com", "password");
        when(userDAO.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.view(userId);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
    }
}
