package com.oop.labs.services;

import com.oop.labs.entities.pointEntity;
import com.oop.labs.repositories.PointRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PointService {

    private static final Logger logger = LogManager.getLogger(PointService.class);

    private final PointRepository pointRepository;

    public PointService(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    public Optional<pointEntity> findPointById(UUID id) {
        logger.info("Поиск точки по ID: {}", id);
        Optional<pointEntity> point = pointRepository.findById(id);
        point.ifPresent(entity -> logger.debug("Найдена точка с X={}, Y={}", entity.getX_value(), entity.getY_value()));
        return point;
    }

    public List<pointEntity> findPointsByFunctionId(UUID functionId) {
        logger.info("Поиск точек по functionId: {}", functionId);
        List<pointEntity> points = pointRepository.findByFunctionId(functionId);
        logger.debug("Найдено точек: {}", points.size());
        return points;
    }

    public List<pointEntity> findPointsByXValue(double x) {
        logger.info("Поиск точек по X: {}", x);
        List<pointEntity> points = pointRepository.findByxValue(x);
        logger.debug("Найдено точек: {}", points.size());
        return points;
    }

    public List<pointEntity> findPointsByYValue(double y) {
        logger.info("Поиск точек по Y: {}", y);
        List<pointEntity> points = pointRepository.findByyValue(y);
        logger.debug("Найдено точек: {}", points.size());
        return points;
    }

    public List<pointEntity> findAllPointsSortedById(Sort.Direction direction) {
        logger.info("Получение точек, отсортированных по ID ({})", direction);
        return pointRepository.findAll(Sort.by(direction == null ? Sort.Direction.ASC : direction, "id"));
    }

    public List<pointEntity> findAllPointsSortedByFunctionId(Sort.Direction direction) {
        logger.info("Получение точек, отсортированных по functionId ({})", direction);
        return pointRepository.findAll(Sort.by(direction == null ? Sort.Direction.ASC : direction, "functionId"));
    }

    public List<pointEntity> findAllPointsSortedByXValue(Sort.Direction direction) {
        logger.info("Получение точек, отсортированных по xValue ({})", direction);
        return pointRepository.findAll(Sort.by(direction == null ? Sort.Direction.ASC : direction, "xValue"));
    }

    public List<pointEntity> findAllPointsSortedByYValue(Sort.Direction direction) {
        logger.info("Получение точек, отсортированных по yValue ({})", direction);
        return pointRepository.findAll(Sort.by(direction == null ? Sort.Direction.ASC : direction, "yValue"));
    }

    @Transactional
    public pointEntity createPoint(UUID functionId, double xValue, double yValue) {
        logger.info("Создание новой точки для функции: {}, X: {}, Y: {}", functionId, xValue, yValue);
        pointEntity point = new pointEntity();
        point.setFunction_id(functionId);
        point.setX_value(xValue);
        point.setY_value(yValue);
        pointEntity saved = pointRepository.save(point);
        logger.debug("Точка создана с ID: {}", saved.getId());
        return saved;
    }

    @Transactional
    public pointEntity savePoint(pointEntity point) {
        logger.info("Сохранение точки с ID: {}", point.getId());
        pointEntity saved = pointRepository.save(point);
        logger.debug("Точка сохранена: X={}, Y={}", saved.getX_value(), saved.getY_value());
        return saved;
    }

    @Transactional
    public void deletePointById(UUID id) {
        logger.info("Удаление точки по ID: {}", id);
        pointRepository.deleteById(id);
        logger.debug("Точка с ID {} удалена", id);
    }

    @Transactional
    public void deletePoint(pointEntity point) {
        logger.info("Удаление точки с X={}, Y={}", point.getX_value(), point.getY_value());
        pointRepository.delete(point);
        logger.debug("Точка удалена");
    }
}

