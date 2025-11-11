package com.oop.labs.functions_tests;

import com.oop.labs.functions.ConstantFunction;
import com.oop.labs.functions.ZeroFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZeroFunctionTest {

    @Test
    void generalTest() {
        ConstantFunction zero_func = new ZeroFunction();
        assertEquals(0, zero_func.getCnst());
        assertEquals(0, zero_func.apply(15));
        assertEquals(0, zero_func.apply(-88));
    }

}