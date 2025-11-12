package com.oop.labs.services;

import com.oop.labs.entities.functionEntity;
import com.oop.labs.repositories.FunctionRepository;
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
public class FunctionService {

    private static final Logger logger = LogManager.getLogger(FunctionService.class);

    private final FunctionRepository functionRepository;

    public FunctionService(FunctionRepository functionRepository) {
        this.functionRepository = functionRepository;
    }

    public Optional<functionEntity> findFunctionById(UUID id) {
        logger.info("Поиск функции по ID: {}", id);
        Optional<functionEntity> function = functionRepository.findById(id);
        function.ifPresent(entity -> logger.debug("Найдена функция: {}", entity.getName()));
        return function;
    }

    public List<functionEntity> findFunctionsByName(String name) {
        logger.info("Поиск функций по имени: {}", name);
        List<functionEntity> result = functionRepository.findByName(name);
        logger.debug("Найдено функций: {}", result.size());
        return result;
    }

    public List<functionEntity> findFunctionsByAuthor(UUID authorId) {
        logger.info("Поиск функций по автору: {}", authorId);
        List<functionEntity> result = functionRepository.findByAuthorId(authorId);
        logger.debug("Найдено функций: {}", result.size());
        return result;
    }

    public List<functionEntity> findFunctionsByType(String type) {
        logger.info("Поиск функций по типу: {}", type);
        List<functionEntity> result = functionRepository.findByType(type);
        logger.debug("Найдено функций: {}", result.size());
        return result;
    }

    public List<functionEntity> findAllFunctionsSortedById(Sort.Direction direction) {
        logger.info("Получение функций, отсортированных по ID ({})", direction);
        return functionRepository.findAll(Sort.by(direction == null ? Sort.Direction.ASC : direction, "id"));
    }

    public List<functionEntity> findAllFunctionsSortedByName(Sort.Direction direction) {
        logger.info("Получение функций, отсортированных по name ({})", direction);
        return functionRepository.findAll(Sort.by(direction == null ? Sort.Direction.ASC : direction, "name"));
    }

    public List<functionEntity> findAllFunctionsSortedByType(Sort.Direction direction) {
        logger.info("Получение функций, отсортированных по type ({})", direction);
        return functionRepository.findAll(Sort.by(direction == null ? Sort.Direction.ASC : direction, "type"));
    }

    public List<functionEntity> findAllFunctionsSortedByAuthorId(Sort.Direction direction) {
        logger.info("Получение функций, отсортированных по authorId ({})", direction);
        return functionRepository.findAll(Sort.by(direction == null ? Sort.Direction.ASC : direction, "authorId"));
    }
}

