package com.oop.labs.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

/**
 * DTO для передачи функции с точками
 * На фронтенде точки передаются, но на бэкенде они аппроксимируются полиномом
 */
public class FunctionDTO {
    
    private UUID id;
    
    @NotBlank(message = "Поле 'name' обязательное")
    private String name;
    
    @NotBlank(message = "Поле 'type' обязательное")
    private String type;

    @JsonProperty("author_id")
    private UUID authorId;

    private List<PointDTO> points;
    
    @JsonProperty("storageMode")
    private String storageMode; // "pointwise" или "polynomial"

    public FunctionDTO() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public void setAuthorId(UUID authorId) {
        this.authorId = authorId;
    }

    public List<PointDTO> getPoints() {
        return points;
    }

    public void setPoints(List<PointDTO> points) {
        this.points = points;
    }

    public String getStorageMode() {
        return storageMode;
    }

    public void setStorageMode(String storageMode) {
        this.storageMode = storageMode;
    }

    /**
     * Внутренний класс для представления точки
     */
    public static class PointDTO {
        @JsonProperty("x")
        private double x;

        @JsonProperty("y")
        private double y;

        public PointDTO() {
        }

        public PointDTO(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }
    }
}

