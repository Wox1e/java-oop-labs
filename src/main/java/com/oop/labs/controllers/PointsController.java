package com.oop.labs.controllers;

import com.oop.labs.entities.pointEntity;
import com.oop.labs.entities.functionEntity;
import com.oop.labs.entities.userEntity;
import com.oop.labs.services.FunctionService;
import com.oop.labs.services.PointService;
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
@RequestMapping("/points")
public class PointsController  {

    private static final Logger logger = LogManager.getLogger(PointsController.class);
    private final PointService pointService;
    private final FunctionService functionService;
    private final UserService userService;

    PointsController(PointService pointService, FunctionService functionService, UserService userService) {
        this.pointService = pointService;
        this.functionService = functionService;
        this.userService = userService;
    }

    @PostMapping("/")
    public ResponseEntity<?> save(Authentication authentication, @Valid @RequestBody pointEntity point) {
        String username = authentication.getName();
        Optional<userEntity> user = userService.findUsersByUsername(username);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID functionId = point.getFunction_id();

        Optional<functionEntity> function = functionService.findFunctionById(functionId);
        if (function.isEmpty() || !user.get().getId().equals(function.get().getAuthor_id())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "status", "error",
                            "message", "Access denied to function: " + functionId,
                            "timestamp", System.currentTimeMillis()
                    ));
        }

        pointEntity saved = pointService.savePoint(point);
        logger.info("Сохраняем точку {} для функции {} и пользователя {}", saved.getId(), functionId, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "status", "success",
                "created", true,
                "id", saved.getId(),
                "timestamp", System.currentTimeMillis()
        ));
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> saveMany(Authentication authentication, @Valid @RequestBody List<pointEntity> points) {
        String username = authentication.getName();
        Optional<userEntity> user = userService.findUsersByUsername(username);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (points == null || points.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Список точек пуст",
                    "timestamp", System.currentTimeMillis()
            ));
        }

        UUID functionId = points.get(0).getFunction_id();
        boolean sameFunction = points.stream().allMatch(p -> functionId.equals(p.getFunction_id()));
        if (!sameFunction) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Все точки должны принадлежать одной функции",
                    "timestamp", System.currentTimeMillis()
            ));
        }

        Optional<functionEntity> function = functionService.findFunctionById(functionId);
        if (function.isEmpty() || !user.get().getId().equals(function.get().getAuthor_id())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "status", "error",
                            "message", "Access denied to function: " + functionId,
                            "timestamp", System.currentTimeMillis()
                    ));
        }

        List<pointEntity> saved = pointService.savePoints(points);
        logger.info("Сохранили {} точек для функции {} и пользователя {}", saved.size(), functionId, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "status", "success",
                "created", true,
                "count", saved.size(),
                "timestamp", System.currentTimeMillis()
        ));
    }

    @GetMapping("/")
    public ResponseEntity<?> get(Authentication authentication,
                                 @RequestParam(required = false) UUID functionId,
                                 @RequestParam(required = false) Double x_value,
                                 @RequestParam(required = false) Double y_value) {
        String username = authentication.getName();
        Optional<userEntity> user = userService.findUsersByUsername(username);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID authorId = user.get().getId();
        logger.info("Ищем точку для пользователя {} - functionId: {} | x_value: {} | y_value: {}", authorId, functionId, x_value, y_value);

        try {
            // получаем все функции пользователя
            List<functionEntity> userFunctions = functionService.findFunctionsByAuthor(authorId);
            Set<UUID> userFunctionIds = new HashSet<>();
            for (functionEntity func : userFunctions) {
                userFunctionIds.add(func.getId());
            }

            // если явно передан functionId, то проверяем, что он принадлежит пользователю
            if (functionId != null && !userFunctionIds.contains(functionId)) {
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "found", false,
                        "data", Collections.emptyList(),
                        "count", 0,
                        "timestamp", System.currentTimeMillis()
                ));
            }

            List<pointEntity> points = pointService.findFiltered(functionId, x_value, y_value);

            // фильтруем точки только по функциям текущего пользователя
            List<pointEntity> filteredPoints = new ArrayList<>();
            for (pointEntity p : points) {
                if (userFunctionIds.contains(p.getFunction_id())) {
                    filteredPoints.add(p);
                }
            }

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "found", !filteredPoints.isEmpty(),
                    "data", filteredPoints,
                    "count", filteredPoints.size(),
                    "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            logger.error("Ошибка при поиске точек", e);
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