package functions_tests;

import functions.ConstantFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstantFunctionTest {

    @Test
    void getCnstTest() {
        ConstantFunction const_func = new ConstantFunction(15);
        assertEquals(15, const_func.getCnst());
    }

    @Test
    void applyTest() {
        ConstantFunction const_func = new ConstantFunction(22);
        assertEquals(22, const_func.apply(0));
    }
}