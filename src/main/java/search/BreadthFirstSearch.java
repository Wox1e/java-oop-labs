package search;

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

public class BreadthFirstSearch {

    private final UserDAO userDAO;
    private final FunctionDAO functionDAO;
    private final PointDAO pointDAO;
    private static final Logger logger = LogManager.getLogger(BreadthFirstSearch.class);

    public BreadthFirstSearch(UserDAO UserDAO, FunctionDAO functionDAO, PointDAO pointDAO) {
        this.userDAO = UserDAO;
        this.functionDAO = functionDAO;
        this.pointDAO = pointDAO;
    }

    public List<Object> findAll() {
        logger.info("Поиск по всем данным в ширину (BFS)");

        List<UserEntity> users = userDAO.findAll();
        logger.info("Найдено пользователей: {}", users.size());
        List<Object> result = new LinkedList<>(users);

        List<FunctionEntity> allFunctions = new LinkedList<>();
        for (UserEntity user : users) {
            List<FunctionEntity> userFunctions = functionDAO.findByAuthorId(user.getId());
            logger.info("Для пользователя {} найдено функций: {}", user.getUsername(), userFunctions.size());
            allFunctions.addAll(userFunctions);
        }
        logger.info("Всего функций собрано: {}", allFunctions.size());
        result.addAll(allFunctions);

        List<PointEntity> allPoints = new LinkedList<>();
        for (FunctionEntity function : allFunctions) {
            List<PointEntity> functionPoints = pointDAO.findByFunctionId(function.getId());
            logger.info("Для функции {} найдено точек: {}", function.getName(), functionPoints.size());
            allPoints.addAll(functionPoints);
        }
        logger.info("Всего точек собрано: {}", allPoints.size());
        result.addAll(allPoints);

        logger.info("BFS-поиск завершён, всего элементов: {}", result.size());
        return result;
    }

    public List<Object> findAllForUser(UserEntity user) {
        logger.info("Поиск в ширину для пользователя с ID {}", user.getId());
        List<Object> result = new LinkedList<>();
        result.add(user);

        List<FunctionEntity> functions = functionDAO.findByAuthorId(user.getId());
        logger.info("Найдено функций пользователя: {}", functions.size());
        result.addAll(functions);

        List<PointEntity> userPoints = new LinkedList<>();
        for (FunctionEntity function : functions) {
            List<PointEntity> points = pointDAO.findByFunctionId(function.getId());
            logger.info("Для функции {} найдено точек: {}", function.getName(), points.size());
            userPoints.addAll(points);
        }
        logger.info("Всего точек пользователя: {}", userPoints.size());
        result.addAll(userPoints);

        logger.info("BFS-поиск для пользователя завершён, всего элементов: {}", result.size());
        return result;
    }
}