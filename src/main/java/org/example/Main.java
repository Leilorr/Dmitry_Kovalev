package org.example;

import org.example.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            logger.info("Starting User Service application");
            UserService userService = new UserService();
            userService.start();
        } catch (Exception e) {
            logger.error("Application error", e);
            System.err.println("An error occurred: " + e.getMessage());
        }
    }
}