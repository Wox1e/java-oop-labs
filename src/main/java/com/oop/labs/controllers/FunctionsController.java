package com.oop.labs.controllers;

import com.oop.labs.concurrent.MultiplyingTask;
import com.oop.labs.entities.functionEntity;
import com.oop.labs.entities.userEntity;
import com.oop.labs.services.FunctionService;
import com.oop.labs.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/functions")
public class FunctionsController{

    private static final Logger logger = LogManager.getLogger(FunctionsController.class);
    private final FunctionService service;
    private final UserService userService;

    FunctionsController(FunctionService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @PostMapping("/")
    public ResponseEntity<?> save(Authentication authentication, @RequestBody functionEntity function) {
        String username = authentication.getName();
        Optional<userEntity> user = userService.findUsersByUsername(username);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        function.setAuthor_id(user.get().getId());
        functionEntity saved = service.saveFunction(function);
        logger.info("Сохраняем функцию {} для пользователя {}", saved.getName(), username);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "status", "success",
                "created", true,
                "id", saved.getId(),
                "timestamp", System.currentTimeMillis()
        ));
    }

    @GetMapping("/")
    public ResponseEntity<?> get(Authentication authentication,
                                 @RequestParam(required = false) String type,
                                 @RequestParam(required = false) String name) {
        logger.info("auth: {}", authentication.getName());
        String username =  authentication.getName();
        Optional<userEntity> user = userService.findUsersByUsername(username);


        if(user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        UUID authorId = user.get().getId();

        logger.info("Ищем функцию - authorId: {} | type: {} | name: {}", authorId, type, name);

        try {
            List<functionEntity> functions = service.findFiltered(authorId, type, name);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "found", functions != null && !functions.isEmpty(),
                    "data", functions != null ? functions : Collections.emptyList(),
                    "count", functions != null ? functions.size() : 0,
                    "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            logger.error("Ошибка при поиске функций", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "found", false,
                            "message", "Internal server error",
                            "timestamp", System.currentTimeMillis()
                    ));
        }
    }


    @GetMapping("/{function_id}")
    public ResponseEntity<?> getByID(Authentication authentication, @PathVariable UUID function_id) {
        logger.info("Ищем функцию по id {}", function_id);

        try {
            String username = authentication.getName();
            Optional<userEntity> user = userService.findUsersByUsername(username);

            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Optional<functionEntity> function = service.findFunctionById(function_id);

            if (function.isEmpty() || !user.get().getId().equals(function.get().getAuthor_id())) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of(
                                "status", "error",
                                "found", false,
                                "message", "Function not found with id: " + function_id,
                                "timestamp", System.currentTimeMillis()
                        ));
            }

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "found", true,
                    "data", function.get(),
                    "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            logger.error("Ошибка при поиске функции по id: {}", function_id, e);
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