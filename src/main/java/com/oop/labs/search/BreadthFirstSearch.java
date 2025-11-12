package com.oop.labs.search;

import com.oop.labs.repositories.FunctionRepository;
import com.oop.labs.repositories.PointRepository;
import com.oop.labs.repositories.UserRepository;
import com.oop.labs.entities.functionEntity;
import com.oop.labs.entities.pointEntity;
import com.oop.labs.entities.userEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class BreadthFirstSearch {

    private final UserRepository userRepository;
    private final FunctionRepository functionRepository;
    private final PointRepository pointRepository;
    private static final Logger logger = LogManager.getLogger(BreadthFirstSearch.class);

    public BreadthFirstSearch(UserRepository userRepository, FunctionRepository functionRepository, PointRepository pointRepository) {
        this.userRepository = userRepository;
        this.functionRepository = functionRepository;
        this.pointRepository = pointRepository;
    }

    public List<Object> findAll() {
        logger.info("Поиск по всем данным в ширину (BFS)");

        List<userEntity> users = userRepository.findAll();
        logger.info("Найдено пользователей: {}", users.size());
        List<Object> result = new LinkedList<>(users);

        List<functionEntity> allFunctions = new LinkedList<>();
        for (userEntity user : users) {
            List<functionEntity> userFunctions = functionRepository.findByAuthorId(user.getId());
            logger.info("Для пользователя {} найдено функций: {}", user.getUsername(), userFunctions.size());
            allFunctions.addAll(userFunctions);
        }
        logger.info("Всего функций собрано: {}", allFunctions.size());
        result.addAll(allFunctions);

        List<pointEntity> allPoints = new LinkedList<>();
        for (functionEntity function : allFunctions) {
            List<pointEntity> functionPoints = pointRepository.findByFunctionId(function.getId());
            logger.info("Для функции {} найдено точек: {}", function.getName(), functionPoints.size());
            allPoints.addAll(functionPoints);
        }
        logger.info("Всего точек собрано: {}", allPoints.size());
        result.addAll(allPoints);

        logger.info("BFS-поиск завершён, всего элементов: {}", result.size());
        return result;
    }

    public List<Object> findAllForUser(userEntity user) {
        logger.info("Поиск в ширину для пользователя с ID {}", user.getId());
        List<Object> result = new LinkedList<>();
        result.add(user);

        List<functionEntity> functions = functionRepository.findByAuthorId(user.getId());
        logger.info("Найдено функций пользователя: {}", functions.size());
        result.addAll(functions);

        List<pointEntity> userPoints = new LinkedList<>();
        for (functionEntity function : functions) {
            List<pointEntity> points = pointRepository.findByFunctionId(function.getId());
            logger.info("Для функции {} найдено точек: {}", function.getName(), points.size());
            userPoints.addAll(points);
        }
        logger.info("Всего точек пользователя: {}", userPoints.size());
        result.addAll(userPoints);

        logger.info("BFS-поиск для пользователя завершён, всего элементов: {}", result.size());
        return result;
    }
}