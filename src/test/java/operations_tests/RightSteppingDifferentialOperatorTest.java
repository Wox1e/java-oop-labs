package operations_tests;

import functions.ConstantFunction;
import functions.MathFunction;
import operations.LeftSteppingDifferentialOperator;
import operations.RightSteppingDifferentialOperator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RightSteppingDifferentialOperatorTest {

    @Test
    void testDerive() {
        RightSteppingDifferentialOperator diffOperator = new RightSteppingDifferentialOperator(10e-3);
        MathFunction func = new ConstantFunction(5);
        assertEquals(0, diffOperator.derive(func).apply(45));

        MathFunction linearFunc = (x) -> 5*x;
        assertEquals(5, diffOperator.derive(linearFunc).apply(228), 10e-3);

        MathFunction squaredFunc = (x) -> Math.pow(x, 2);
        double x = 5.89;
        assertEquals(2*x, diffOperator.derive(squaredFunc).apply(x), 10e-2);

    }
}