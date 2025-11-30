package com.oop.labs.manual.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Set;

public class User {
    @JsonProperty("id")
    private long id;
    @JsonProperty("username")
    private String username;
    @JsonProperty("password_hash")
    private String password_hash;
    @JsonProperty("roles")
    private Set<String> roles = new HashSet<>();

    public boolean equals(User obj) {
        return username.equals(obj.username) && id == obj.id;
    }

    public User() {
    }

    public User(String username, String passwordHash) {
        this.password_hash = passwordHash;
        this.username = username;
        this.roles.add("USER");
    }

    public User(long id, String username, String passwordHash) {
        this.id = id;
        this.password_hash = passwordHash;
        this.username = username;
        this.roles.add("USER");
    }

    public User(long id, String username, String passwordHash, Set<String> roles) {
        this.id = id;
        this.password_hash = passwordHash;
        this.username = username;
        this.roles = roles;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword_hash(String passwordHash) {
        this.password_hash = passwordHash;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
