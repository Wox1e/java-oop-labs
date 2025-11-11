package com.oop.labs;

import dao.UserDAO;
import entities.UserEntity;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.*;

public class UserDAOTest {

    private Connection connection;
    private UserDAO userDAO;

    @BeforeEach
    void setup() throws SQLException {
        // Инициализация в памяти H2 базы
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        try (Statement stmt = connection.createStatement()) {
            // Создание таблицы users
            stmt.execute("CREATE TABLE users (" +
                    "id UUID DEFAULT RANDOM_UUID() PRIMARY KEY," +
                    "username VARCHAR(50) UNIQUE NOT NULL," +
                    "password_hash VARCHAR(255) NOT NULL" +
                    ")");
        }
        userDAO = new UserDAO(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS users");
        }
    }

    @Test
    void testCreateAndFindById() {
        UserEntity user = new UserEntity(UUID.randomUUID(), "testUser", "passHash");
        UUID id = userDAO.create(user);
        Optional<UserEntity> found = userDAO.findById(id);
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("testUser", found.get().getUsername());
    }

    @Test
    void testFindAll() {
        userDAO.create(new UserEntity(UUID.randomUUID(), "user1", "hash1"));
        userDAO.create(new UserEntity(UUID.randomUUID(), "user2", "hash2"));
        List<UserEntity> users = userDAO.findAll();
        Assertions.assertEquals(2, users.size());
    }

    @Test
    void testFindByUsername() {
        userDAO.create(new UserEntity(UUID.randomUUID(), "userFind", "hashFind"));
        Optional<UserEntity> userOpt = userDAO.findByUsername("userFind");
        Assertions.assertTrue(userOpt.isPresent());
        Assertions.assertEquals("userFind", userOpt.get().getUsername());
    }

    @Test
    void testFindByNonexistentId() {
        Optional<UserEntity> userOpt = userDAO.findById(UUID.randomUUID());
        Assertions.assertFalse(userOpt.isPresent());
    }
}

