package com.oop.labs.operations;

import com.oop.labs.functions.MathFunction;

public interface DifferentialOperator<T> extends MathFunction {
    T derive(T function);
}
