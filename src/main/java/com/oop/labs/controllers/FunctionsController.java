package com.oop.labs.controllers;

import com.oop.labs.entities.functionEntity;
import com.oop.labs.services.FunctionService;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/functions")
public class FunctionsController{

    private final FunctionService service;

    FunctionsController(FunctionService service) {
        this.service = service;
    }

    @PostMapping("/")
    public functionEntity save(@RequestBody functionEntity function) {
        service.saveFunction(function);
        return function;
    }

    @GetMapping("/")
    public List<functionEntity> get(@RequestParam(required = false) UUID authorId,
                                    @RequestParam(required = false) String type,
                                    @RequestParam(required = false) String name) {

        return service.findFiltered(authorId, type, name);
    }


    @GetMapping("/{function_id}")
    public Optional<functionEntity> getByID(@PathVariable UUID function_id) {
        return service.findFunctionById(function_id);
    }

    @DeleteMapping("/{function_id}")
    public String deleteByID(@PathVariable UUID function_id) {
        service.deleteFunctionById(function_id);
        return "Deleted: " + function_id.toString();
    }


}