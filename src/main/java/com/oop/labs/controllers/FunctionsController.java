package com.oop.labs.controllers;

import com.oop.labs.concurrent.MultiplyingTask;
import com.oop.labs.entities.functionEntity;
import com.oop.labs.services.FunctionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/functions")
public class FunctionsController{

    private static final Logger logger = LogManager.getLogger(FunctionsController.class);
    private final FunctionService service;

    FunctionsController(FunctionService service) {
        this.service = service;
    }

    @PostMapping("/")
    public functionEntity save(@RequestBody functionEntity function) {
        service.saveFunction(function);
        logger.info("Сохраняем функцию {}", function.getName());
        return function;
    }

    @GetMapping("/")
    public ResponseEntity<?> get(@RequestParam(required = false) UUID authorId,
                                 @RequestParam(required = false) String type,
                                 @RequestParam(required = false) String name) {
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
    public ResponseEntity<?> getByID(@PathVariable UUID function_id) {
        logger.info("Ищем функцию по id {}", function_id);

        try {
            Optional<functionEntity> function = service.findFunctionById(function_id);

            if (function.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
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