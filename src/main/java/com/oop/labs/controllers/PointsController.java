package com.oop.labs.controllers;

import com.oop.labs.entities.functionEntity;
import com.oop.labs.entities.pointEntity;
import com.oop.labs.services.FunctionService;
import com.oop.labs.services.PointService;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/points")
public class PointsController  {

    private final PointService service;

    PointsController(PointService service) {
        this.service = service;
    }

    @PostMapping("/")
    public pointEntity save(@RequestBody pointEntity point) {
        service.savePoint(point);
        return point;
    }

    @GetMapping("/")
    public List<pointEntity> get(@RequestParam(required = false) UUID functionId,
                                    @RequestParam(required = false) Double x_value,
                                    @RequestParam(required = false) Double y_value) {
        return service.findFiltered(functionId, x_value, y_value);
    }



}