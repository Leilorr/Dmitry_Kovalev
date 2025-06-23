package org.example.service;

import org.example.dao.UserDao;
import org.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private Scanner scanner;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("Test User", "test@example.com", 25);
        testUser.setId(1L);
    }

    @Test
    void createUser_ShouldSaveUser_WhenValidInput() {
        when(scanner.nextLine())
                .thenReturn("Test User")
                .thenReturn("test@example.com");
        when(scanner.nextInt()).thenReturn(25);
        doNothing().when(userDao).save(any(User.class));

        User result = userService.createUser("Test User", "test@example.com", 25);

        assertThat(result).isNull();
        verify(userDao).save(any(User.class));
    }

    @Test
    void viewAllUsers_ShouldPrintAllUsers_WhenUsersExist() {
        List<User> users = Arrays.asList(
                new User("User1", "user1@example.com", 20),
                new User("User2", "user2@example.com", 30)
        );
        when(userDao.findAll()).thenReturn(users);

        userService.viewAllUsers();

        verify(userDao).findAll();
    }

    @Test
    void viewUserById_ShouldPrintUser_WhenUserExists() {
        when(scanner.nextLine()).thenReturn("1");
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));

        userService.viewUserById();

        verify(userDao).findById(1L);
    }

    @Test
    void updateUser_ShouldUpdateUser_WhenUserExists() {
        when(scanner.nextLine())
                .thenReturn("1")
                .thenReturn("")
                .thenReturn("new@example.com")
                .thenReturn("30");
        when(scanner.nextInt()).thenReturn(30);
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userDao).update(any(User.class));

        userService.updateUser();

        verify(userDao).update(testUser);
        assertThat(testUser.getEmail()).isEqualTo("new@example.com");
        assertThat(testUser.getAge()).isEqualTo(30);
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        when(scanner.nextLine()).thenReturn("1");
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userDao).delete(testUser);

        userService.deleteUser();

        verify(userDao).delete(testUser);
    }

    @Test
    void readIntInput_ShouldReturnInt_WhenValidInput() {
        when(scanner.nextLine())
                .thenReturn("abc")
                .thenReturn("123");

        int result = userService.readIntInput();

        assertThat(result).isEqualTo(123);
    }

    @Test
    void readLongInput_ShouldReturnLong_WhenValidInput() {
        when(scanner.nextLine())
                .thenReturn("xyz")
                .thenReturn("456");

        long result = userService.readLongInput();

        assertThat(result).isEqualTo(456L);
    }
}