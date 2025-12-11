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
import jakarta.validation.Valid;

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
    public ResponseEntity<?> save(Authentication authentication, @Valid @RequestBody functionEntity function) {
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

    @DeleteMapping("/{function_id}")
    public ResponseEntity<?> deleteFunction(Authentication authentication, @PathVariable UUID function_id) {
        logger.info("Попытка удаления функции с id: {}", function_id);
        
        try {
            String username = authentication.getName();
            Optional<userEntity> user = userService.findUsersByUsername(username);
            
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            UUID userId = user.get().getId();
            
            // Проверяем существование функции и принадлежность пользователю
            Optional<functionEntity> function = service.findFunctionById(function_id);
            
            if (function.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "status", "error",
                                "message", "Function not found",
                                "timestamp", System.currentTimeMillis()
                        ));
            }
            
            if (!function.get().getAuthor_id().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "status", "error",
                                "message", "Access denied",
                                "timestamp", System.currentTimeMillis()
                        ));
            }
            
            // Удаляем функцию
            service.deleteFunctionById(function_id);
            
            logger.info("Функция с id {} удалена пользователем {}", function_id, username);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Function deleted successfully",
                    "timestamp", System.currentTimeMillis()
            ));
            
        } catch (Exception e) {
            logger.error("Ошибка при удалении функции с id: {}", function_id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Internal server error",
                            "timestamp", System.currentTimeMillis()
                    ));
        }
    }

    @PutMapping("/{function_id}")
    public ResponseEntity<?> updateFunction(Authentication authentication,
                                            @PathVariable UUID function_id,
                                            @RequestBody Map<String, Object> payload) {
        logger.info("Попытка обновления функции с id: {}", function_id);

        try {
            String username = authentication.getName();
            Optional<userEntity> user = userService.findUsersByUsername(username);

            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Optional<functionEntity> functionOpt = service.findFunctionById(function_id);
            if (functionOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "status", "error",
                                "message", "Function not found",
                                "timestamp", System.currentTimeMillis()
                        ));
            }

            functionEntity function = functionOpt.get();
            if (!function.getAuthor_id().equals(user.get().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "status", "error",
                                "message", "Access denied",
                                "timestamp", System.currentTimeMillis()
                        ));
            }

            // Обновляем только разрешенные поля
            if (payload.containsKey("name")) {
                String name = Objects.toString(payload.get("name"), "").trim();
                if (name.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "status", "error",
                            "message", "Name cannot be empty",
                            "timestamp", System.currentTimeMillis()
                    ));
                }
                function.setName(name);
            }

            if (payload.containsKey("type")) {
                String type = Objects.toString(payload.get("type"), "").trim();
                if (type.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "status", "error",
                            "message", "Type cannot be empty",
                            "timestamp", System.currentTimeMillis()
                    ));
                }
                function.setType(type);
            }

            functionEntity saved = service.saveFunction(function);
            logger.info("Функция {} обновлена пользователем {}", saved.getId(), username);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "updated", true,
                    "data", saved,
                    "timestamp", System.currentTimeMillis()
            ));

        } catch (Exception e) {
            logger.error("Ошибка при обновлении функции с id: {}", function_id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Internal server error",
                            "timestamp", System.currentTimeMillis()
                    ));
        }
    }
}