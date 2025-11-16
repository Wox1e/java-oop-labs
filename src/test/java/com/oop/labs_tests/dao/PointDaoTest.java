package com.oop.labs_tests.dao;

import com.oop.labs.manual.dao.FunctionDao;
import com.oop.labs.manual.dao.PointDao;
import com.oop.labs.manual.dao.UserDao;
import com.oop.labs.manual.dto.Function;
import com.oop.labs.manual.dto.Point;
import com.oop.labs.manual.dto.User;
import com.oop.labs_tests.MockDBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PointDaoTest {
    private static PointDao pointDao;
    private static FunctionDao functionDao;
    private static UserDao userDao;
    private static Connection testConnection;
    private static long testUserId;
    private static long testFunctionId;

    @BeforeAll
    static void setup() {

        testConnection = MockDBConnection.getTestConnection();

        pointDao = new PointDao(testConnection);
        functionDao = new FunctionDao(testConnection);
        userDao = new UserDao(testConnection);
    }

    @BeforeEach
    void setupTestData() {
        User user = new User("test_user_" + System.currentTimeMillis(), "test_hash");
        testUserId = userDao.create(user);

        Function function = new Function("test_function_" + System.currentTimeMillis(), "test_type", testUserId);
        testFunctionId = functionDao.create(function);
    }

    @AfterEach
    void clearTables() throws SQLException {
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("DELETE FROM points");
            stmt.execute("DELETE FROM functions");
            stmt.execute("DELETE FROM users");
        }
    }

    @Test
    void pointCreateTest() {
        Point point = new Point(testFunctionId, 1.0, 2.0);
        long id1 = pointDao.create(point);
        assertTrue(id1 > 0);

        point = new Point(testFunctionId, 2.0, 4.0);
        long id2 = pointDao.create(point);
        assertTrue(id2 > id1);

        Point retrieved = pointDao.findById(id1).get();
        assertEquals(1.0, retrieved.getXValue(), 0.001);
        assertEquals(2.0, retrieved.getYValue(), 0.001);
    }


    @Test
    void findExistingPointById() {
        Point expectedPoint = new Point(testFunctionId, 3.0, 9.0);
        long pointId = pointDao.create(expectedPoint);

        Optional<Point> result = pointDao.findById(pointId);

        assertTrue(result.isPresent());
        Point point = result.get();
        assertEquals(pointId, point.getId());
        assertEquals(testFunctionId, point.getFunctionId());
        assertEquals(3.0, point.getXValue(), 0.001);
        assertEquals(9.0, point.getYValue(), 0.001);
    }

    @Test
    void findNonExistingPointById() {
        Optional<Point> result = pointDao.findById(999L);
        assertFalse(result.isPresent());
    }

    @Test
    void findByFunctionId() throws SQLException {
        Function function2 = new Function("test_function_2_" + System.currentTimeMillis(), "type2", testUserId);
        int function2Id = functionDao.create(function2);

        Point point1 = new Point(testFunctionId, 1.0, 1.0);
        Point point2 = new Point(testFunctionId, 2.0, 4.0);
        Point point3 = new Point(function2Id, 3.0, 9.0); // Другая функция

        pointDao.create(point1);
        pointDao.create(point2);
        pointDao.create(point3);

        List<Point> result = pointDao.findByFunctionId(testFunctionId);
        assertEquals(2, result.size());
    }

    @Test
    void findAllInEmptyTable() throws SQLException {
        List<Point> result = pointDao.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll() throws SQLException {
        List<Point> expectedPoints = List.of(
                new Point(testFunctionId, 1.0, 1.0),
                new Point(testFunctionId, 2.0, 4.0),
                new Point(testFunctionId, 3.0, 9.0)
        );

        for (Point point : expectedPoints) {
            pointDao.create(point);
        }

        List<Point> result = pointDao.findAll();
        assertEquals(3, result.size());
    }

    @Test
    void updatePoint() throws SQLException {
        Point point = new Point(testFunctionId, 1.0, 1.0);
        long pointId = pointDao.create(point);

        Point updatedPoint = new Point(pointId, testFunctionId, 5.0, 25.0);
        boolean result = pointDao.update(updatedPoint);

        assertTrue(result);

        Optional<Point> retrievedPoint = pointDao.findById(pointId);
        assertTrue(retrievedPoint.isPresent());
        assertEquals(5.0, retrievedPoint.get().getXValue(), 0.001);
        assertEquals(25.0, retrievedPoint.get().getYValue(), 0.001);
    }

    @Test
    void deletePoint() throws SQLException {
        Point point = new Point(testFunctionId, 1.0, 1.0);
        long pointId = pointDao.create(point);

        boolean result = pointDao.delete(pointId);
        assertTrue(result);

        Optional<Point> retrievedPoint = pointDao.findById(pointId);
        assertFalse(retrievedPoint.isPresent());
    }

    @Test
    void deleteByFunctionId() throws SQLException {
        Point point1 = new Point(testFunctionId, 1.0, 1.0);
        Point point2 = new Point(testFunctionId, 2.0, 4.0);

        pointDao.create(point1);
        pointDao.create(point2);

        boolean result = pointDao.deleteByFunctionId(testFunctionId);
        assertTrue(result);

        List<Point> points = pointDao.findByFunctionId(testFunctionId);
        assertTrue(points.isEmpty());
    }
}