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
import java.util.UUID;

@Service
public class DepthFirstSearch {

    private final UserRepository userRepository;
    private final FunctionRepository functionRepository;
    private final PointRepository pointRepository;
    private static final Logger logger = LogManager.getLogger(DepthFirstSearch.class);

    public DepthFirstSearch(FunctionRepository functionRepository,  PointRepository pointRepository, UserRepository userRepository) {
        this.functionRepository = functionRepository;
        this.pointRepository = pointRepository;
        this.userRepository = userRepository;
    }

    public List<pointEntity> getUserPoints(userEntity user){
        UUID authorId = user.getId();
        logger.info("Ищем точки для пользователя с ID {}",  authorId);
        List<functionEntity> functions = functionRepository.findByAuthorId(authorId);
        logger.info("Найдено ф-ций: {}", functions.size());

        List<pointEntity> points = new LinkedList<>();

        for(functionEntity function : functions){
            UUID functionId = function.getId();
            logger.info("Обрабатываем ф-цию с ID {}",  functionId);
            List<pointEntity> funcPoints = pointRepository.findByFunctionId(functionId);
            logger.info("Найдено точек {}",   funcPoints.size());
            points.addAll(funcPoints);
        }
        logger.info("Поиск завершен");
        logger.info("Найдено всего точек {}", points.size());
        return points;
    }

    public List<pointEntity> getAllPointsDepthFirst() {
        List<pointEntity> allPoints = new LinkedList<>();
        List<userEntity> users = userRepository.findAll();
        for (userEntity user : users) {
            allPoints.addAll(getUserPoints(user));
        }
        return allPoints;
    }

}