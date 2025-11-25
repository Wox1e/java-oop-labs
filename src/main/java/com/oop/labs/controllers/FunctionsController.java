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
public class FunctionsController extends Controller {

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
        List<functionEntity> result = service.findAllFunctionsSortedByAuthorId(Sort.Direction.ASC);

        if (authorId != null) {
            List<functionEntity> byAuthor = service.findFunctionsByAuthor(authorId);
            result = super.getCommonElements(result, byAuthor);
        }

        if (type != null && !type.trim().isEmpty()) {
            List<functionEntity> byType = service.findFunctionsByType(type.trim());
            result = getCommonElements(result, byType);
        }

        if (name != null && !name.trim().isEmpty()) {
            List<functionEntity> byName = service.findFunctionsByName(name.trim());
            result = getCommonElements(result, byName);
        }

        return result;
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