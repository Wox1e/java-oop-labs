package com.oop.labs.manual.dto;

public class User {
    private int id;
    private String username;
    private String passwordHash;

    public boolean equals(User obj) {
        return username.equals(obj.username) && id == obj.id;
    }

    public User(String username, String passwordHash) {
        this.passwordHash = passwordHash;
        this.username = username;
    }

    public User(int id, String username, String passwordHash) {
        this.id = id;
        this.passwordHash = passwordHash;
        this.username = username;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
