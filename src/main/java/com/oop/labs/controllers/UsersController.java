package com.oop.labs.controllers;

import com.oop.labs.entities.userEntity;
import com.oop.labs.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UserService service;

    UsersController(UserService service) {
        this.service = service;
    }

    @PostMapping("/")
    public userEntity save(@RequestBody userEntity user) {
        Optional<userEntity> existingUser = service.findUsersByUsername(user.getUsername());
        if (existingUser.isPresent()){
            return existingUser.get();
        }

        service.saveUser(user);
        return user;
    }

    @GetMapping("/{username}")
    public Optional<userEntity> getByID(@PathVariable String username) {
        return service.findUsersByUsername(username);
    }



}