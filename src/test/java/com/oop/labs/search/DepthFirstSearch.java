package com.oop.labs.search;

import dao.FunctionDAO;
import dao.PointDAO;
import dao.UserDAO;
import entities.FunctionEntity;
import entities.PointEntity;
import entities.UserEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class DepthFirstSearch {

    private final UserDAO userDAO;
    private final FunctionDAO functionDao;
    private final PointDAO pointDao;
    private static final Logger logger = LogManager.getLogger(DepthFirstSearch.class);

    public DepthFirstSearch(FunctionDAO funcDao,  PointDAO pointDao, UserDAO userDAO) {
        this.functionDao = funcDao;
        this.pointDao = pointDao;
        this.userDAO = userDAO;
    }

    public List<PointEntity> getUserPoints(UserEntity user){
        UUID authorId = user.getId();
        logger.info("Ищем точки для пользователя с ID {}",  authorId);
        List<FunctionEntity> functions = functionDao.findByAuthorId(authorId);
        logger.info("Найдено ф-ций: {}", functions.size());

        List<PointEntity> points = new LinkedList<>();

        for(FunctionEntity function : functions){
            UUID functionId = function.getId();
            logger.info("Обрабатываем ф-цию с ID {}",  functionId);
            List<PointEntity> funcPoints = pointDao.findByFunctionId(functionId);
            logger.info("Найдено точек {}",   funcPoints.size());
            points.addAll(funcPoints);
        }
        logger.info("Поиск завершен");
        logger.info("Найдено всего точек {}", points.size());
        return points;
    }

    public List<PointEntity> getAllPointsDepthFirst() {
        List<PointEntity> allPoints = new LinkedList<>();
        List<UserEntity> users = userDAO.findAll();
        for (UserEntity user : users) {
            allPoints.addAll(getUserPoints(user));
        }
        return allPoints;
    }

}