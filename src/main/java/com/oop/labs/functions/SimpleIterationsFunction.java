package com.oop.labs.functions;

import java.lang.Math;

public class SimpleIterationsFunction implements MathFunction{

    final private double epsilon;
    final private MathFunction func;

    public SimpleIterationsFunction(double epsilon, MathFunction func){
        this.epsilon = epsilon;
        this.func = func;
    }

    @Override
    public double apply(double x) {
        return SimpleIterationMethod(x, epsilon, func);
    }

    private double SimpleIterationMethod(double x_n, double epsilon, MathFunction func) {
        double x_n2;
        do {
            x_n2 = func.apply(x_n);
            x_n = func.apply(x_n2);
        } while (Math.abs(x_n2 - x_n) > epsilon);

        return x_n;
    }
}