package com.oop.labs_tests;

import com.oop.labs.manual.dao.UserDao;
import org.junit.jupiter.api.BeforeAll;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MockDBConnection {

    private static Connection  testConnection;

    MockDBConnection() {
        try {
            testConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "mysecretpassword");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static Connection getTestConnection() {
        if (testConnection == null){
            try {
                testConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "mysecretpassword");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return testConnection;
    }

    public static void dropTables() throws SQLException {
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS points CASCADE");
            stmt.execute("DROP TABLE IF EXISTS functions CASCADE");
            stmt.execute("DROP TABLE IF EXISTS users CASCADE");
        }
    }


    public static void createTables() throws SQLException {
        try (Statement stmt = testConnection.createStatement()) {
            // Создаем таблицу users
            String createUsersTableSQL = """
                CREATE TABLE users(
                    id serial primary key,
                    username varchar(64) unique not null,
                    password_hash varchar(64) not null
                )
                """;
            stmt.execute(createUsersTableSQL);

            // Затем создаем таблицу functions
            String createFunctionsTableSQL = """
                CREATE TABLE functions(
                    id serial primary key,
                    name varchar(32) not null,
                    type varchar(32) not null,
                    author_id int,
                    FOREIGN KEY (author_id) REFERENCES users(id)
                )
                """;
            stmt.execute(createFunctionsTableSQL);

            // Затем создаем таблицу points
            String createPointsTableSQL = """
                CREATE TABLE points(
                    id bigserial primary key,
                    function_id int,
                    x_value decimal,
                    y_value decimal,
                    FOREIGN KEY (function_id) REFERENCES functions(id)
                )
                """;
            stmt.execute(createPointsTableSQL);
        }
    }



}
