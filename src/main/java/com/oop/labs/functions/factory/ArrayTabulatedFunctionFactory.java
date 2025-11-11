package com.oop.labs.functions.factory;

import com.oop.labs.functions.ArrayTabulatedFunction;
import com.oop.labs.functions.TabulatedFunction;

public class ArrayTabulatedFunctionFactory implements TabulatedFunctionFactory {
    @Override
    public TabulatedFunction create(double[] xValues, double[] yValues){
        return new ArrayTabulatedFunction(xValues, yValues);
    }
}
