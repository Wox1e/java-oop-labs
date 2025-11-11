package com.oop.labs;

import dao.FunctionDAO;
import entities.FunctionEntity;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.*;

public class FunctionDAOTest {
    private static FunctionDAO functionDAO;
    private static Connection connection;

    @BeforeAll
    static void setup() throws Exception {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS functions");
            stmt.execute("""
                CREATE TABLE functions(
                    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                    name VARCHAR(64) NOT NULL,
                    type VARCHAR(64) NOT NULL,
                    author_id UUID NOT NULL
                )
            """);
        }
        functionDAO = new FunctionDAO(connection);
    }

    @AfterEach
    void clearTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM functions");
        }
    }

    @Test
    void createAndFindById() {
        UUID authorId = UUID.randomUUID();
        FunctionEntity function = new FunctionEntity(UUID.randomUUID(), "f", "poly", authorId);
        UUID id = functionDAO.create(function);
        Optional<FunctionEntity> found = functionDAO.findById(id);
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("f", found.get().getName());
    }

    @Test
    void findByName() {
        UUID authorId = UUID.randomUUID();
        FunctionEntity function = new FunctionEntity(UUID.randomUUID(), "g", "trig", authorId);
        functionDAO.create(function);
        Optional<FunctionEntity> found = functionDAO.findByName("g");
        Assertions.assertTrue(found.isPresent());
    }

    @Test
    void findAllInEmptyTable() {
        List<FunctionEntity> found = functionDAO.findAll();
        Assertions.assertTrue(found.isEmpty());
    }

    @Test
    void findAll() {
        UUID authorId = UUID.randomUUID();
        FunctionEntity f1 = new FunctionEntity(UUID.randomUUID(), "f1", "poly", authorId);
        FunctionEntity f2 = new FunctionEntity(UUID.randomUUID(), "f2", "poly", authorId);
        functionDAO.create(f1);
        functionDAO.create(f2);
        List<FunctionEntity> found = functionDAO.findAll();
        Assertions.assertEquals(2, found.size());
    }
}

