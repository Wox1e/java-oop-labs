package com.oop.labs.functions_tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import com.oop.labs.functions.*;
import com.oop.labs.functions.factory.*;
import com.oop.labs.operations.TabulatedDifferentialOperator;
import static org.junit.jupiter.api.Assertions.*;

public class TabulatedDifferentialOperatorTest {

    private TabulatedDifferentialOperator operator;
    private double[] xValues;
    private double[] yValues;

    @BeforeEach
    void setUp() {
        operator = new TabulatedDifferentialOperator();
        xValues = new double[]{1.0, 2.0, 3.0};
        yValues = new double[]{1.0, 4.0, 9.0};
    }

    @Test
    void testDefaultConstructor() {
        assertInstanceOf(ArrayTabulatedFunctionFactory.class, operator.getFactory());
    }

    @Test
    void testConstructorWithFactory() {
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        TabulatedDifferentialOperator customOperator = new TabulatedDifferentialOperator(factory);
        assertEquals(factory, customOperator.getFactory());
    }

    @Test
    void testGetSetFactory() {
        TabulatedFunctionFactory newFactory = new LinkedListTabulatedFunctionFactory();
        operator.setFactory(newFactory);
        assertEquals(newFactory, operator.getFactory());
    }

    @Test
    void testDeriveWithArrayFactory() {
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        assertInstanceOf(ArrayTabulatedFunction.class, derivative);
        assertEquals(3.0, derivative.getY(0), 1e-9);
        assertEquals(5.0, derivative.getY(1), 1e-9);
        assertEquals(5.0, derivative.getY(2), 1e-9);
    }

    @Test
    void testDeriveWithLinkedListFactory() {
        operator.setFactory(new LinkedListTabulatedFunctionFactory());
        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        assertInstanceOf(LinkedListTabulatedFunction.class, derivative);
        assertEquals(3.0, derivative.getY(0), 1e-9);
        assertEquals(5.0, derivative.getY(1), 1e-9);
        assertEquals(5.0, derivative.getY(2), 1e-9);
    }

    @Test
    void testDeriveWithTwoPoints() {
        double[] simpleX = {0.0, 1.0};
        double[] simpleY = {0.0, 3.0};

        TabulatedFunction function = new ArrayTabulatedFunction(simpleX, simpleY);
        TabulatedFunction derivative = operator.derive(function);

        assertEquals(3.0, derivative.getY(0), 1e-9);
        assertEquals(3.0, derivative.getY(1), 1e-9);
    }
}