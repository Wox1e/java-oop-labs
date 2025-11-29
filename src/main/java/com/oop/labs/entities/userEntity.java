package com.oop.labs.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Entity
public class userEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;
    
    @NotBlank(message = "Поле 'username' обязательное")
    private String username;
    
    @NotBlank(message = "Поле 'password' обязательное")
    private String passwordHash;


    public UUID getId(){
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword_hash(){
        return this.passwordHash;
    }

    public void setPassword_hash(String password_hash) {
        this.passwordHash = password_hash;
    }

}
