package com.oop.labs.controllers;

import com.oop.labs.entities.functionEntity;
import com.oop.labs.entities.pointEntity;
import com.oop.labs.services.FunctionService;
import com.oop.labs.services.PointService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/points")
public class PointsController  {

    private static final Logger logger = LogManager.getLogger(PointsController.class);
    private final PointService service;

    PointsController(PointService service) {
        this.service = service;
    }

    @PostMapping("/")
    public pointEntity save(@RequestBody pointEntity point) {
        service.savePoint(point);
        logger.info("Сохраняем точку {}", point.getId());
        return point;
    }

    @GetMapping("/")
    public ResponseEntity<?> get(@RequestParam(required = false) UUID functionId,
                                 @RequestParam(required = false) Double x_value,
                                 @RequestParam(required = false) Double y_value) {
        logger.info("Ищем точку - functionId: {} | x_value: {} | y_value: {}", functionId, x_value, y_value);

        try {
            List<pointEntity> points = service.findFiltered(functionId, x_value, y_value);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "found", points != null && !points.isEmpty(),
                    "data", points != null ? points : Collections.emptyList(),
                    "count", points != null ? points.size() : 0,
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