package com.oop.labs.controllers;

import com.oop.labs.entities.userEntity;
import com.oop.labs.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UsersController {

    private static final Logger logger = LogManager.getLogger(UsersController.class);
    private final UserService service;

    UsersController(UserService service) {
        this.service = service;
    }

    @PostMapping("/")
    public userEntity save(@RequestBody userEntity user) {
        logger.info("Сохраняем пользователя {}", user);
        Optional<userEntity> existingUser = service.findUsersByUsername(user.getUsername());
        if (existingUser.isPresent()){
            logger.info("Пользователь уже существует");
            return existingUser.get();
        }

        service.saveUser(user);
        logger.info("Пользователь сохранён");
        return user;
    }

    @GetMapping("/{username}")
    public Optional<userEntity> getByUsername(@PathVariable String username) {
        logger.info("Ищем пользователя по username: {}", username);
        return service.findUsersByUsername(username);
    }



}