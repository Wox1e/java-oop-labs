package com.oop.labs.operations;

import com.oop.labs.functions.MathFunction;

abstract public class SteppingDifferentialOperator implements DifferentialOperator<MathFunction>{
    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        if (step <= 0 || Double.isInfinite(step) || Double.isNaN(step)) throw new IllegalArgumentException();
        this.step = step;
    }


    protected double step;


    public SteppingDifferentialOperator(double step){
        setStep(step);
    }
}
