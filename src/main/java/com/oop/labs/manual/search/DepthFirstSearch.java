package com.oop.labs.manual.search;

import com.oop.labs.manual.dao.UserDao;
import com.oop.labs.manual.dto.Function;
import com.oop.labs.manual.dto.Point;
import com.oop.labs.manual.dto.User;

import com.oop.labs.manual.dao.FunctionDao;
import com.oop.labs.manual.dao.PointDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.LinkedList;
import java.util.List;

public class DepthFirstSearch {

    private FunctionDao functionDao;
    private PointDao pointDao;
    private static final Logger logger = LogManager.getLogger(DepthFirstSearch.class);

    public DepthFirstSearch(FunctionDao funcDao,  PointDao pointDao) {
        this.functionDao = funcDao;
        this.pointDao = pointDao;
    }

    public List<Point> getUserPoints(User user){
        int authorId = user.getId();
        logger.info("Ищем точки для пользователя с ID {}",  authorId);
        List<Function> functions = functionDao.findByAuthorId(authorId);
        logger.info("Найдено ф-ций: {}", functions.size());

        List<Point> points = new LinkedList<>();

        for(Function function : functions){
            int functionId = function.getId();
            logger.info("Обрабатываем ф-цию с ID {}",  functionId);
            List<Point> funcPoints = pointDao.findByFunctionId(functionId);
            logger.info("Найдено точек {}",   funcPoints.size());
            points.addAll(funcPoints);
        }
        logger.info("Поиск завершен");
        logger.info("Найдено всего точек {}", points.size());
        return points;
    }
}
