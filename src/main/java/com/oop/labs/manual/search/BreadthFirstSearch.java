package com.oop.labs.manual.search;

import com.oop.labs.manual.dao.FunctionDao;
import com.oop.labs.manual.dao.PointDao;
import com.oop.labs.manual.dao.UserDao;
import com.oop.labs.manual.dto.Function;
import com.oop.labs.manual.dto.Point;
import com.oop.labs.manual.dto.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BreadthFirstSearch {

    private UserDao userDao;
    private FunctionDao functionDao;
    private PointDao pointDao;
    private static final Logger logger = LogManager.getLogger(BreadthFirstSearch.class);

    public BreadthFirstSearch(UserDao userDao, FunctionDao functionDao, PointDao pointDao) {
        this.userDao = userDao;
        this.functionDao = functionDao;
        this.pointDao = pointDao;
    }

    public List<Object> findAll() {
        logger.info("Поиск по всем данным в ширину (BFS)");
        List<Object> result = new LinkedList<>();

        List<User> users = userDao.findAll();
        logger.info("Найдено пользователей: {}", users.size());
        result.addAll(users);

        List<Function> allFunctions = new LinkedList<>();
        for (User user : users) {
            List<Function> userFunctions = functionDao.findByAuthorId(user.getId());
            logger.info("Для пользователя {} найдено функций: {}", user.getUsername(), userFunctions.size());
            allFunctions.addAll(userFunctions);
        }
        logger.info("Всего функций собрано: {}", allFunctions.size());
        result.addAll(allFunctions);

        List<Point> allPoints = new LinkedList<>();
        for (Function function : allFunctions) {
            List<Point> functionPoints = pointDao.findByFunctionId(function.getId());
            logger.info("Для функции {} найдено точек: {}", function.getName(), functionPoints.size());
            allPoints.addAll(functionPoints);
        }
        logger.info("Всего точек собрано: {}", allPoints.size());
        result.addAll(allPoints);

        logger.info("BFS-поиск завершён, всего элементов: {}", result.size());
        return result;
    }

    public List<Object> findAllForUser(User user) {
        logger.info("Поиск в ширину для пользователя с ID {}", user.getId());
        List<Object> result = new LinkedList<>();
        result.add(user);

        List<Function> functions = functionDao.findByAuthorId(user.getId());
        logger.info("Найдено функций пользователя: {}", functions.size());
        result.addAll(functions);

        List<Point> userPoints = new LinkedList<>();
        for (Function function : functions) {
            List<Point> points = pointDao.findByFunctionId(function.getId());
            logger.info("Для функции {} найдено точек: {}", function.getName(), points.size());
            userPoints.addAll(points);
        }
        logger.info("Всего точек пользователя: {}", userPoints.size());
        result.addAll(userPoints);

        logger.info("BFS-поиск для пользователя завершён, всего элементов: {}", result.size());
        return result;
    }
}

