package com.oop.labs.operations;

import com.oop.labs.concurrent.SynchronizedTabulatedFunction;
import com.oop.labs.functions.TabulatedFunction;
import com.oop.labs.functions.factory.ArrayTabulatedFunctionFactory;
import com.oop.labs.functions.factory.TabulatedFunctionFactory;

public class TabulatedDifferentialOperator implements DifferentialOperator<TabulatedFunction> {

    private TabulatedFunctionFactory factory;

    public TabulatedDifferentialOperator(TabulatedFunctionFactory factory){
        this.factory = factory;
    }

    public TabulatedDifferentialOperator(){
        factory = new ArrayTabulatedFunctionFactory();
    }

    public TabulatedFunctionFactory getFactory(){
        return factory;
    }

    public void setFactory(TabulatedFunctionFactory factory){
        this.factory = factory;
    }

    public TabulatedFunction deriveSynchronously(TabulatedFunction function){
        SynchronizedTabulatedFunction syncFunction;

        if(! (function instanceof SynchronizedTabulatedFunction)) {
            syncFunction = new SynchronizedTabulatedFunction(function);
        }else {
            syncFunction = (SynchronizedTabulatedFunction) function;
        }

        return syncFunction.doSynchronously(this::derive);
    }

    @Override
    public double apply(double x) {
        return 0;
    }

    @Override
    public TabulatedFunction derive(TabulatedFunction function) {
        int count = function.getCount();
        double[] xValues = new double[count];
        double[] yValues = new double[count];

        for(int i = 0; i < count - 1; i++) {
            xValues[i] = function.getX(i);
            yValues[i] = (function.getY(i + 1) - function.getY(i)) /
                    (function.getX(i + 1) - function.getX(i));
        }

        xValues[count - 1] = function.getX(count - 1);
        yValues[count - 1] = yValues[count - 2];

        return factory.create(xValues, yValues);
    }
}
