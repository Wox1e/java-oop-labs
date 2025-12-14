package com.oop.labs.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
public class functionEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;
    
    @NotBlank(message = "Поле 'name' обязательное")
    private String name;
    
    @NotBlank(message = "Поле 'type' обязательное")
    private String type;

    private UUID authorId;

    // Храним коэффициенты полинома в JSONB поле PostgreSQL
    // Функция хранится в полиномиальной форме через аппроксимацию (не поточечно!)
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String polynomialCoefficients; // JSON массив коэффициентов [a0, a1, a2, ...]
    
    // Диапазон x для восстановления точек
    private Double xMin;
    private Double xMax;
    
    // Количество исходных точек (для восстановления)
    private Integer originalPointCount;


    public UUID getId(){
        return this.id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }


    public String getType(){
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }


    public UUID getAuthor_id() {
        return this.authorId;
    }

    public void setAuthor_id(UUID author_id) {
        this.authorId = author_id;
    }

    public String getPolynomialCoefficients() {
        return polynomialCoefficients;
    }

    public void setPolynomialCoefficients(String polynomialCoefficients) {
        this.polynomialCoefficients = polynomialCoefficients;
    }

    public Double getXMin() {
        return xMin;
    }

    public void setXMin(Double xMin) {
        this.xMin = xMin;
    }

    public Double getXMax() {
        return xMax;
    }

    public void setXMax(Double xMax) {
        this.xMax = xMax;
    }

    public Integer getOriginalPointCount() {
        return originalPointCount;
    }

    public void setOriginalPointCount(Integer originalPointCount) {
        this.originalPointCount = originalPointCount;
    }
}
