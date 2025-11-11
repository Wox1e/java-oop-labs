package com.oop.labs;

import dao.PointDAO;
import entities.PointEntity;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.*;

public class PointDAOTest {
    private static PointDAO pointDAO;
    private static Connection connection;

    @BeforeAll
    static void setup() throws Exception {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS points");
            stmt.execute("""
                CREATE TABLE points(
                    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                    function_id UUID NOT NULL,
                    x_value DOUBLE NOT NULL,
                    y_value DOUBLE NOT NULL
                )
            """);
        }
        pointDAO = new PointDAO(connection);
    }

    @AfterEach
    void clearTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM points");
        }
    }

    @Test
    void createAndFindById() {
        UUID functionId = UUID.randomUUID();
        PointEntity point = new PointEntity(UUID.randomUUID(), functionId, 1.0, 2.0);
        UUID id = pointDAO.create(point);
        Optional<PointEntity> found = pointDAO.findById(id);
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(1.0, found.get().getX_value());
    }

    @Test
    void findByFunctionId() {
        UUID functionId = UUID.randomUUID();
        PointEntity p1 = new PointEntity(UUID.randomUUID(), functionId, 0, 0);
        PointEntity p2 = new PointEntity(UUID.randomUUID(), functionId, 1, 1);
        pointDAO.create(p1);
        pointDAO.create(p2);
        List<PointEntity> found = pointDAO.findByFunctionId(functionId);
        Assertions.assertEquals(2, found.size());
    }

    @Test
    void findAllInEmptyTable() {
        List<PointEntity> found = pointDAO.findAll();
        Assertions.assertTrue(found.isEmpty());
    }

    @Test
    void findAll() {
        PointEntity p1 = new PointEntity(UUID.randomUUID(), UUID.randomUUID(), 1, 2);
        PointEntity p2 = new PointEntity(UUID.randomUUID(), UUID.randomUUID(), 2, 3);
        pointDAO.create(p1);
        pointDAO.create(p2);
        List<PointEntity> found = pointDAO.findAll();
        Assertions.assertEquals(2, found.size());
    }
}

