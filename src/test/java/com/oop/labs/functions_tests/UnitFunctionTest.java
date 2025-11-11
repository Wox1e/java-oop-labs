package com.oop.labs.functions_tests;

import com.oop.labs.functions.ConstantFunction;
import com.oop.labs.functions.UnitFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnitFunctionTest {

    @Test
    void generalTest() {
        ConstantFunction unit_func = new UnitFunction();
        assertEquals(1, unit_func.getCnst());
        assertEquals(1, unit_func.apply(5));
        assertEquals(1, unit_func.apply(0));
    }

}