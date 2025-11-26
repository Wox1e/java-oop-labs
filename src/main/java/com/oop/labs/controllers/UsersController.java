package com.oop.labs.controllers;

import com.oop.labs.entities.userEntity;
import com.oop.labs.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        logger.info("Сохраняем пользователя {}", user.getUsername());
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
    public ResponseEntity<?> getByUsername(@PathVariable String username) {
        logger.info("Ищем пользователя по username: {}", username);

        try {
            Optional<userEntity> user = service.findUsersByUsername(username);
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", System.currentTimeMillis());

            if (user.isEmpty()) {
                response.put("status", "success");
                response.put("found", false);
                response.put("message", "User not found with username: " + username);
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }

            response.put("status", "success");
            response.put("found", true);
            response.put("data", user.get());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Ошибка при поиске пользователя {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "found", false,
                            "message", "Internal server error",
                            "timestamp", System.currentTimeMillis()
                    ));
        }
    }


}