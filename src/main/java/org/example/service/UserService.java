package org.example.service;

import org.example.dao.UserDao;
import org.example.dao.UserDaoImpl;
import org.example.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);
    private final UserDao userDao = new UserDaoImpl();
    private final Scanner scanner = new Scanner(System.in);
    private User User;

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\nUser Management System");
            System.out.println("1. Create User");
            System.out.println("2. View All Users");
            System.out.println("3. View User by ID");
            System.out.println("4. Update User");
            System.out.println("5. Delete User");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice = readIntInput();

            switch (choice) {
                case 1:
                    createUser("Test User", "test@example.com", 25);
                    break;
                case 2:
                    viewAllUsers();
                    break;
                case 3:
                    viewUserById();
                    break;
                case 4:
                    updateUser();
                    break;
                case 5:
                    deleteUser();
                    break;
                case 6:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public User createUser(String testUser, String mail, int i) {
        System.out.println("\nCreate New User");
        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter age: ");
        int age = readIntInput();

        try {
            User user = new User(name, email, age);
            userDao.save(user);
            System.out.println("User created successfully!");
            logger.info("Created user: {}", user);
        } catch (Exception e) {
            System.out.println("Error creating user: " + e.getMessage());
            logger.error("Error creating user", e);
        }
        return User;
    }

    public void viewAllUsers() {
        System.out.println("\nAll Users:");
        try {
            List<User> users = userDao.findAll();
            if (users.isEmpty()) {
                System.out.println("No users found.");
            } else {
                users.forEach(System.out::println);
            }
            logger.info("Viewed all users. Count: {}", users.size());
        } catch (Exception e) {
            System.out.println("Error retrieving users: " + e.getMessage());
            logger.error("Error retrieving users", e);
        }
    }

    public void viewUserById() {
        System.out.print("\nEnter user ID: ");
        Long id = readLongInput();

        try {
            Optional<User> user = userDao.findById(id);
            if (user.isPresent()) {
                System.out.println(user);
                logger.info("Viewed user by ID: {}", id);
            } else {
                System.out.println("User not found with ID: " + id);
            }
        } catch (Exception e) {
            System.out.println("Error retrieving user: " + e.getMessage());
            logger.error("Error retrieving user with ID: {}", id, e);
        }
    }

    public void updateUser() {
        System.out.print("\nEnter user ID to update: ");
        Long id = readLongInput();

        try {
            Optional<User> user = userDao.findById(id);
            if (user.isEmpty()) {
                System.out.println("User not found with ID: " + id);
                return;
            }

            System.out.println("Current user details: " + user);

            System.out.print("Enter new name (leave blank to keep current): ");
            String name = scanner.nextLine();
            if (!name.isEmpty()) {
                user.get().setName(name);
            }

            System.out.print("Enter new email (leave blank to keep current): ");
            String email = scanner.nextLine();
            if (!email.isEmpty()) {
                user.get().setEmail(email);
            }

            System.out.print("Enter new age (0 to keep current): ");
            int age = readIntInput();
            if (age != 0) {
                user.get().setAge(age);
            }

            userDao.update(user.orElse(null));
            System.out.println("User updated successfully!");
            logger.info("Updated user: {}", user);
        } catch (Exception e) {
            System.out.println("Error updating user: " + e.getMessage());
            logger.error("Error updating user with ID: {}", id, e);
        }
    }

    public void deleteUser() {
        System.out.print("\nEnter user ID to delete: ");
        Long id = readLongInput();

        try {
            Optional<User> user = userDao.findById(id);
            if (user.isEmpty()) {
                System.out.println("User not found with ID: " + id);
                return;
            }

            userDao.delete(user.orElse(null));
            System.out.println("User deleted successfully!");
            logger.info("Deleted user with ID: {}", id);
        } catch (Exception e) {
            System.out.println("Error deleting user: " + e.getMessage());
            logger.error("Error deleting user with ID: {}", id, e);
        }
    }

    public int readIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    public Long readLongInput() {
        while (true) {
            try {
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
}