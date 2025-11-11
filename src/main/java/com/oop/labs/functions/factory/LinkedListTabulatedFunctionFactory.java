package com.oop.labs.functions.factory;

import com.oop.labs.functions.LinkedListTabulatedFunction;
import com.oop.labs.functions.TabulatedFunction;

public class LinkedListTabulatedFunctionFactory implements TabulatedFunctionFactory{
    @Override
    public TabulatedFunction create(double[] xValues, double[] yValues){
        return new LinkedListTabulatedFunction(xValues, yValues);
    }
}
