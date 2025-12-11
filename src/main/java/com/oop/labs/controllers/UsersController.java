package com.oop.labs.controllers;

import com.oop.labs.entities.userEntity;
import com.oop.labs.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

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
    public ResponseEntity<?> save(@Valid @RequestBody userEntity user) {
        logger.info("Сохраняем пользователя {}", user.getUsername());
        Optional<userEntity> existingUser = service.findUsersByUsername(user.getUsername());
        if (existingUser.isPresent()){
            logger.info("Пользователь уже существует");
            return ResponseEntity.ok()
                    .body(Map.of(
                            "status", "success",
                            "created", false,
                            "id", existingUser.get().getId(),
                            "timestamp", System.currentTimeMillis()
                    ));
        }
        // Принимаем "password" из user, сохраняем через сервис (service.saveUser сам захеширует)
        service.saveUser(user);
        logger.info("Пользователь сохранён");
        return ResponseEntity.ok()
                .body(Map.of(
                        "status", "success",
                        "created", true,
                        "id", user.getId(),
                        "timestamp", System.currentTimeMillis()
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Username and password are required",
                "timestamp", System.currentTimeMillis()
            ));
        }
        Optional<userEntity> userOpt = service.findUsersByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "status", "error",
                "message", "Неправильные данные для входа",
                "timestamp", System.currentTimeMillis()
            ));
        }
        userEntity user = userOpt.get();
        boolean valid = service.checkPassword(user, password);
        if (!valid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "status", "error",
                "message", "Неправильные данные для входа",
                "timestamp", System.currentTimeMillis()
            ));
        }
        // build response map with only public data
        Map<String,Object> data = Map.of(
            "id", user.getId(),
            "username", user.getUsername()
        );
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "user", data,
            "timestamp", System.currentTimeMillis()
        ));
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getByUsername(
            @PathVariable String username,
            @RequestHeader(value = "X-API-KEY", required = false) String apiKey
    ) {
        final String FIXED_API_KEY = "my-secret-key";
        if (apiKey == null || !apiKey.equals(FIXED_API_KEY)) {
            logger.warn("Попытка доступа с неверным или отсутствующим ключом X-API-KEY");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "status", "error",
                            "message", "Invalid or missing API key",
                            "timestamp", System.currentTimeMillis()
                    ));
        }
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