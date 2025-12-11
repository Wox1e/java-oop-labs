package com.oop.labs.services;

import com.oop.labs.entities.userEntity;
import com.oop.labs.repositories.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<userEntity> findUserById(UUID id) {
        logger.info("Поиск пользователя по ID: {}", id);
        Optional<userEntity> user = userRepository.findById(id);
        user.ifPresent(entity -> logger.debug("Найден пользователь: {}", entity.getUsername()));
        return user;
    }

    public Optional<userEntity> findUsersByUsername(String username) {
        logger.info("Поиск пользователей по username: {}", username);
        Optional<userEntity> users = userRepository.findByUsername(username);
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

    @Transactional
    public userEntity createUser(String username, String password) {
        logger.info("Создание нового пользователя с username: {}", username);
        userEntity user = new userEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userEntity saved = userRepository.save(user);
        logger.debug("Пользователь создан с ID: {}", saved.getId());
        return saved;
    }

    @Transactional
    public userEntity saveUser(userEntity user) {
        logger.info("Сохранение пользователя с ID: {}", user.getId());
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
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

    public boolean checkPassword(userEntity user, String password) {
        if (user == null || password == null) return false;
        return passwordEncoder.matches(password, user.getPassword());
    }
}

