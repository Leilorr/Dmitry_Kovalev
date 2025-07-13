package org.example.dao;

import org.example.model.User;
import org.example.util.HibernateUtil;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDaoImplIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgresqlContainer =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpass");

    private static UserDao userDao;

    @BeforeAll
    static void beforeAll() {
        System.setProperty("hibernate.connection.url", postgresqlContainer.getJdbcUrl());
        System.setProperty("hibernate.connection.username", postgresqlContainer.getUsername());
        System.setProperty("hibernate.connection.password", postgresqlContainer.getPassword());

        userDao = new UserDaoImpl();
    }

    @AfterAll
    static void afterAll() {
        HibernateUtil.shutdown();
    }

    @Test
    @Order(1)
    void shouldSaveUser() {
        User user = new User("John Doe", "john@example.com", 30);
        userDao.save(user);

        assertThat(user.getId()).isNotNull();
    }

    @Test
    @Order(2)
    void shouldFindUserById() {
        User user = new User("Jane Doe", "jane@example.com", 25);
        userDao.save(user);

        Optional<User> foundUser = userDao.findById(user.getId());
        assertThat(foundUser)
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u.getEmail()).isEqualTo("jane@example.com"));
    }

    @Test
    @Order(3)
    void shouldUpdateUser() {
        User user = new User("Old Name", "old@example.com", 40);
        userDao.save(user);

        user.setName("New Name");
        user.setEmail("new@example.com");
        userDao.update(user);

        Optional<User> updatedUser = userDao.findById(user.getId());
        assertThat(updatedUser)
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u.getName()).isEqualTo("New Name");
                    assertThat(u.getEmail()).isEqualTo("new@example.com");
                });
    }

    @Test
    @Order(4)
    void shouldDeleteUser() {
        User user = new User("To Delete", "delete@example.com", 99);
        userDao.save(user);

        userDao.delete(user);

        Optional<User> deletedUser = userDao.findById(user.getId());
        assertThat(deletedUser).isEmpty();
    }

    @Test
    @Order(5)
    void shouldFindAllUsers() {
        userDao.save(new User("User1", "user1@example.com", 20));
        userDao.save(new User("User2", "user2@example.com", 30));

        assertThat(userDao.findAll()).hasSizeGreaterThanOrEqualTo(2);
    }
}