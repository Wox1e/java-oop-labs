package com.oop.labs.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
public class pointEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;
    
    @NotNull(message = "Поле 'functionId' обязательное")
    @JsonProperty("function_id")
    private UUID functionId;
    
    @NotNull(message = "Поле 'xValue' обязательное")
    @JsonProperty("x_value")
    private double xValue;

    @NotNull(message = "Поле 'yValue' обязательное")
    @JsonProperty("y_value")
    private double yValue;

    public UUID getId(){
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }


    public UUID getFunction_id(){
        return this.functionId;
    }

    public void setFunction_id(UUID function_id) {
        this.functionId = function_id;
    }



    public double getX_value(){
        return this.xValue;
    }

    public void setX_value(double x_value) {
        this.xValue = x_value;
    }

    public double getY_value() {
        return this.yValue;
    }

    public void setY_value(double y_value) {
        this.yValue = y_value;
    }


}
