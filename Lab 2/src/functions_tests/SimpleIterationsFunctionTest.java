package functions_tests;

import functions.MathFunction;
import functions.SimpleIterationsFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleIterationsFunctionTest {

    @Test
    void linearEquasionTest() {
        MathFunction linearFunc = (x) -> 2;        // x = 2
        double accuracy = 0.0001;
        MathFunction simpleIterationFunc = new SimpleIterationsFunction(accuracy, linearFunc);
        assertEquals(2, simpleIterationFunc.apply(0));
    }

    @Test
    void squaredEquasionTest() {
        MathFunction squaredFunc = (x) -> (x + 2/x)/2;        // x = (x + 2/x)/2
        double accuracy = 0.001;
        MathFunction simpleIterationFunc = new SimpleIterationsFunction(accuracy, squaredFunc);
        double res = simpleIterationFunc.apply(1);
        double target = 1.4167;
        assertTrue( Math.abs(res - target) < 0.01);
    }


}