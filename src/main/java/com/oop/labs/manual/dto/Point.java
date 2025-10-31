package com.oop.labs.manual.dto;


public class Point {
    private long id;
    private int functionId;
    private double xValue;
    private double yValue;

    public Point(int functionId, double xValue, double yValue) {
        this.functionId = functionId;
        this.xValue = xValue;
        this.yValue = yValue;
    }

    public Point(long id, int functionId, double xValue, double yValue) {
        this.id = id;
        this.functionId = functionId;
        this.xValue = xValue;
        this.yValue = yValue;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getFunctionId() {
        return functionId;
    }

    public void setFunctionId(int functionId) {
        this.functionId = functionId;
    }

    public double getXValue() {
        return xValue;
    }

    public void setXValue(double xValue) {
        this.xValue = xValue;
    }

    public double getYValue() {
        return yValue;
    }

    public void setYValue(double yValue) {
        this.yValue = yValue;
    }

}