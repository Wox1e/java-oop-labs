package com.oop.labs.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class pointEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;
    private UUID functionId;
    private double xValue;
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
