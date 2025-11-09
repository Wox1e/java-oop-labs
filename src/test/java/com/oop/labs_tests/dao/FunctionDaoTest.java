package com.oop.labs_tests.dao;

import com.oop.labs.manual.dao.FunctionDao;
import com.oop.labs.manual.dao.UserDao;
import com.oop.labs.manual.dto.Function;
import com.oop.labs.manual.dto.User;
import com.oop.labs_tests.MockDBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class FunctionDaoTest {
    private static FunctionDao functionDao;
    private static UserDao userDao;
    private static Connection testConnection;
    private static int testUserId;

    @BeforeAll
    static void setup() {

        testConnection = MockDBConnection.getTestConnection();

        functionDao = new FunctionDao(testConnection);
        userDao = new UserDao(testConnection);

        try {
            dropTables();
            createTables();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setupTestData() throws SQLException {
        // Создаем уникального тестового пользователя для каждого теста
        User user = new User("test_user_" + System.currentTimeMillis(), "test_hash");
        testUserId = userDao.create(user);
    }

    @AfterEach
    void clearTables() throws SQLException {
        // Очищаем в правильном порядке из-за foreign key constraints
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("DELETE FROM points");
            stmt.execute("DELETE FROM functions");
            stmt.execute("DELETE FROM users");
        }
    }

    static void dropTables() throws SQLException {
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS points CASCADE");
            stmt.execute("DROP TABLE IF EXISTS functions CASCADE");
            stmt.execute("DROP TABLE IF EXISTS users CASCADE");
        }
    }

    static void createTables() throws SQLException {
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

    @Test
    void functionCreateTest() {
        Function function = new Function("sin(x)", "trigonometric", testUserId);
        functionDao.create(function);

        function = new Function("x^2", "polynomial", testUserId);
        functionDao.create(function);
    }

    @Test
    void findExistingFunctionById() {
        Function expectedFunction = new Function("cos(x)", "trigonometric", testUserId);
        int functionId = functionDao.create(expectedFunction);

        Optional<Function> result = functionDao.findById(functionId);

        assertTrue(result.isPresent());
        Function function = result.get();
        assertEquals(functionId, function.getId());
        assertEquals("cos(x)", function.getName());
        assertEquals("trigonometric", function.getType());
        assertEquals(testUserId, function.getAuthorId());
    }

    @Test
    void findNonExistingFunctionById() {
        Optional<Function> result = functionDao.findById(999);
        assertFalse(result.isPresent());
    }

    @Test
    void findByNameExistingFunction() {
        Function expectedFunction = new Function("test_function", "custom", testUserId);
        functionDao.create(expectedFunction);

        Optional<Function> result = functionDao.findByName("test_function");

        assertTrue(result.isPresent());
        assertEquals("test_function", result.get().getName());
        assertEquals("custom", result.get().getType());
    }

    @Test
    void findByAuthorId() throws SQLException {
        // Создаем второго пользователя
        User user2 = new User("test_user_2_" + System.currentTimeMillis(), "hash2");
        int user2Id = userDao.create(user2);

        Function function1 = new Function("func1", "type1", testUserId);
        Function function2 = new Function("func2", "type2", testUserId);
        Function function3 = new Function("func3", "type3", user2Id);

        functionDao.create(function1);
        functionDao.create(function2);
        functionDao.create(function3);

        List<Function> result = functionDao.findByAuthorId(testUserId);
        assertEquals(2, result.size());
    }

    @Test
    void findAllInEmptyTable() {
        List<Function> result = functionDao.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll() {
        List<Function> expectedFunctions = List.of(
                new Function("function1", "type1", testUserId),
                new Function("function2", "type2", testUserId),
                new Function("function3", "type3", testUserId)
        );

        for (Function function : expectedFunctions) {
            functionDao.create(function);
        }

        List<Function> result = functionDao.findAll();
        assertEquals(3, result.size());
    }

    @Test
    void updateFunction() {
        Function function = new Function("old_name", "old_type", testUserId);
        int functionId = functionDao.create(function);

        Function updatedFunction = new Function(functionId, "new_name", "new_type", testUserId);
        boolean result = functionDao.update(updatedFunction);

        assertTrue(result);

        Optional<Function> retrievedFunction = functionDao.findById(functionId);
        assertTrue(retrievedFunction.isPresent());
        assertEquals("new_name", retrievedFunction.get().getName());
        assertEquals("new_type", retrievedFunction.get().getType());
    }

    @Test
    void deleteFunction() {
        Function function = new Function("to_delete", "type", testUserId);
        int functionId = functionDao.create(function);

        boolean result = functionDao.delete(functionId);
        assertTrue(result);

        Optional<Function> retrievedFunction = functionDao.findById(functionId);
        assertFalse(retrievedFunction.isPresent());
    }
}