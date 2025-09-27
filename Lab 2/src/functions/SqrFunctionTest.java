package functions;

import static org.junit.jupiter.api.Assertions.*;

class SqrFunctionTest {

    SqrFunction sqr_func = new SqrFunction();

    @org.junit.jupiter.api.Test
    void applyPostitiveTest() {
        assertEquals(25,sqr_func.apply(5));
        assertEquals(9,sqr_func.apply(3));
        assertEquals(1,sqr_func.apply(1));
    }


    @org.junit.jupiter.api.Test
    void applyZeroTest() {
        assertEquals(0,sqr_func.apply(0));
    }

    @org.junit.jupiter.api.Test
    void applyNegativeTest() {
        assertEquals(36,sqr_func.apply(-6));
        assertEquals(16,sqr_func.apply(-4));
    }

}