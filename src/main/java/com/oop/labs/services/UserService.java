package com.oop.labs.services;

import com.oop.labs.entities.userEntity;
import com.oop.labs.repositories.UserRepository;
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
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<userEntity> findUserById(UUID id) {
        logger.info("Поиск пользователя по ID: {}", id);
        Optional<userEntity> user = userRepository.findById(id);
        user.ifPresent(entity -> logger.debug("Найден пользователь: {}", entity.getUsername()));
        return user;
    }

    public List<userEntity> findUsersByUsername(String username) {
        logger.info("Поиск пользователей по username: {}", username);
        List<userEntity> users = userRepository.findByUsername(username);
        logger.debug("Найдено пользователей: {}", users.size());
        return users;
    }

    public List<userEntity> findUsersByPasswordHash(String passwordHash) {
        logger.info("Поиск пользователей по passwordHash");
        List<userEntity> users = userRepository.findByPasswordHash(passwordHash);
        logger.debug("Найдено пользователей: {}", users.size());
        return users;
    }

    public List<userEntity> findAllUsersSortedById(Sort.Direction direction) {
        logger.info("Получение пользователей, отсортированных по ID ({})", direction);
        return userRepository.findAll(Sort.by(direction == null ? Sort.Direction.ASC : direction, "id"));
    }

    public List<userEntity> findAllUsersSortedByUsername(Sort.Direction direction) {
        logger.info("Получение пользователей, отсортированных по username ({})", direction);
        return userRepository.findAll(Sort.by(direction == null ? Sort.Direction.ASC : direction, "username"));
    }

    public List<userEntity> findAllUsersSortedByPasswordHash(Sort.Direction direction) {
        logger.info("Получение пользователей, отсортированных по passwordHash ({})", direction);
        return userRepository.findAll(Sort.by(direction == null ? Sort.Direction.ASC : direction, "passwordHash"));
    }

    @Transactional
    public userEntity createUser(String username, String passwordHash) {
        logger.info("Создание нового пользователя с username: {}", username);
        userEntity user = new userEntity();
        user.setUsername(username);
        user.setPassword_hash(passwordHash);
        userEntity saved = userRepository.save(user);
        logger.debug("Пользователь создан с ID: {}", saved.getId());
        return saved;
    }

    @Transactional
    public userEntity saveUser(userEntity user) {
        logger.info("Сохранение пользователя с ID: {}", user.getId());
        userEntity saved = userRepository.save(user);
        logger.debug("Пользователь сохранён: {}", saved.getUsername());
        return saved;
    }

    @Transactional
    public void deleteUserById(UUID id) {
        logger.info("Удаление пользователя по ID: {}", id);
        userRepository.deleteById(id);
        logger.debug("Пользователь с ID {} удалён", id);
    }

    @Transactional
    public void deleteUser(userEntity user) {
        logger.info("Удаление пользователя: {}", user.getUsername());
        userRepository.delete(user);
        logger.debug("Пользователь {} удалён", user.getUsername());
    }
}

