package com.oop.labs_tests.dao;

import com.oop.labs.manual.dao.UserDao;
import com.oop.labs.manual.dto.User;
import com.oop.labs_tests.MockDBConnection;
import org.junit.jupiter.api.*;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest {

    private static UserDao userDao;
    private static Connection testConnection;

    @BeforeAll
    static void setup(){

        testConnection = MockDBConnection.getTestConnection();

        userDao = new UserDao(testConnection);
        try {
            dropTable();
            createTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static void dropTable() throws SQLException {
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("DROP TABLE users CASCADE");
        }
    }


    @AfterEach
    void clearTable() throws SQLException {
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("DELETE FROM users");
        }
    }

    static void createTable() throws SQLException {
        try (Statement stmt = testConnection.createStatement()) {
            String createTableSQL = """
                CREATE TABLE users(
                    id serial primary key,
                    username varchar(64) unique not null,
                    password_hash varchar(64) not null
                )
                """;
            stmt.execute(createTableSQL);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void userCreateTest(){
        User user = new User("user", "test_hash");
        userDao.create(user);
        user.setUsername("user2");
        userDao.create(user);
        user.setUsername("user3");
        userDao.create(user);
    }



    @Test
    void findExistingUserById() {
        User expectedUser = new User("test_user", "password_hash");
        int userId = userDao.create(expectedUser);

        Optional<User> result = userDao.findById(userId);

        assertTrue(result.isPresent());
        User user = result.get();
        assertEquals(userId, user.getId());
        assertEquals("test_user", user.getUsername());
        assertEquals("password_hash", user.getPasswordHash());
    }

    @Test
    void findNonExistingUserById() {
        // Act
        Optional<User> result = userDao.findById(999);
        assertFalse(result.isPresent());
    }

    @Test
    void findByUsernameExistingUser() {
        // Arrange
        User expectedUser = new User("bebeboba", "hehehash");
        userDao.create(expectedUser);

        Optional<User> result = userDao.findByUsername("bebeboba");

        assertTrue(result.isPresent());
        assertEquals("bebeboba", result.get().getUsername());
        assertEquals("hehehash", result.get().getPasswordHash());
    }

    @Test
    void findByUsernameNonExistingUser() {
        Optional<User> result = userDao.findByUsername("nonexistent_user");
        assertFalse(result.isPresent());
    }

    @Test
    void findAllInEmptyTable() {
        List<User> result = userDao.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll() {
        List<User> expectedUsers = List.of(
            new User("user1", "hash1"),
            new User("user2", "hash2"),
            new User("user3", "hash3")
        );

        for (User user: expectedUsers) userDao.create(user);

        List<User> result = userDao.findAll();

        assertEquals(3, result.size());
    }

    @Test
    void createUserDuplicateUsername() {
        // Arrange
        User user1 = new User("duplicate_user", "hash1");
        User user2 = new User("duplicate_user", "hash2");

        userDao.create(user1);
        assertThrows(RuntimeException.class, () -> userDao.create(user2));
    }


}

