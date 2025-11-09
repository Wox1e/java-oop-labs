package com.oop.labs_tests.search;

import com.oop.labs.manual.dao.FunctionDao;
import com.oop.labs.manual.dao.PointDao;
import com.oop.labs.manual.dao.UserDao;
import com.oop.labs.manual.dto.Function;
import com.oop.labs.manual.dto.Point;
import com.oop.labs.manual.dto.User;
import com.oop.labs_tests.MockDBConnection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.oop.labs.manual.search.DepthFirstSearch;
import org.mockito.Mock;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DepthFirstSearchTest {

    public static final Connection connection = MockDBConnection.getTestConnection();
    public static DepthFirstSearch depthFirstSearch;
    static UserDao userDao;
    static PointDao pointDao;
    static FunctionDao functionDao;

    @BeforeAll
    static void setup(){
        userDao = new UserDao(connection);
        pointDao = new PointDao(connection);
        functionDao = new FunctionDao(connection);
        depthFirstSearch = new DepthFirstSearch(functionDao, pointDao);
    }

    @Test
    void getUserPointTest() throws SQLException {
        MockDBConnection.dropTables();
        MockDBConnection.createTables();
        User user = new User("Biba", "Bibibabovich");
        int id = userDao.create(user);
        user.setId(id);

        List<Point> expected = new LinkedList<>();

        for (int i = 0; i < 25; i++) {
            int funcID = functionDao.create(new Function("hehehaha", "trigonomentr", id));
            for (int j = 0; j < 50; j++) {
                Point point = new Point(funcID, j, j+12);
                expected.add(point);
                pointDao.create(point);
            }
        }

        List<Point> actual = depthFirstSearch.getUserPoints(user);

        assertEquals(expected.size(), actual.size());

        for(Point point : expected){
            assertTrue(actual.contains(point));
        }
    }

}