package com.oop.labs.manual.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    static {
        try {
            Class.forName("org.h2.Driver");
            initializeDatabase();
        } catch (Exception e) {
            throw new RuntimeException("H2 Database initialization failed", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static void initializeDatabase() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50), " +
                    "password_hash VARCHAR(100))");

            stmt.execute("CREATE TABLE IF NOT EXISTS functions (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(50), " +
                    "type VARCHAR(50), " +
                    "author_id BIGINT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS points (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "function_id BIGINT, " +
                    "x_value DOUBLE, " +
                    "y_value DOUBLE)");

            System.out.println("H2 Database initialized successfully!");
        }
    }
}