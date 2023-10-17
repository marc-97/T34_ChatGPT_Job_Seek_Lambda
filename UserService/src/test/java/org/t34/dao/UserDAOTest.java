package org.t34.dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.t34.entity.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDAOTest {
    private static SessionFactory sessionFactory;
    private UserDAO userDAO;

    @BeforeAll
    public static void setUp() {
        Configuration configuration = new Configuration()
                .setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                .setProperty("hibernate.connection.url", "jdbc:h2:mem:t34;DB_CLOSE_DELAY=-1;NON_KEYWORDS=USER;IGNORECASE=TRUE;MODE=MySQL")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .addAnnotatedClass(User.class);

        sessionFactory = configuration.buildSessionFactory();
    }

    @AfterAll
    public static void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    public void testSaveUser() {
        userDAO = new UserDAO(sessionFactory);

        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("password");
        userDAO.save(user);

        Optional<User> savedUser = userDAO.findByEmail("user@example.com");
        assertTrue(savedUser.isPresent());
    }

    @Test
    public void testFindById() {
        userDAO = new UserDAO(sessionFactory);

        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("password");
        userDAO.save(user);

        Optional<User> foundUser = userDAO.findById(user.getUserId().intValue());

        assertTrue(foundUser.isPresent());
        assertEquals("user@example.com", foundUser.get().getEmail());
    }

    @Test
    public void testUpdateUser() {
        userDAO = new UserDAO(sessionFactory);

        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("password");
        userDAO.save(user);

        user.setEmail("updated@example.com");
        User updatedUser = userDAO.update(user);

        Optional<User> foundUser = userDAO.findById(updatedUser.getUserId().intValue());

        assertTrue(foundUser.isPresent());
        assertEquals("updated@example.com", foundUser.get().getEmail());
    }
}
