package functions_tests;

import functions.factory.ArrayTabulatedFunctionFactory;
import functions.ArrayTabulatedFunction;
import functions.TabulatedFunction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ArrayTabulatedFunctionFactoryTest {

    @Test
    void testCreateReturnsArrayTabulatedFunction() {
        ArrayTabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};

        TabulatedFunction result = factory.create(xValues, yValues);

        assertInstanceOf(ArrayTabulatedFunction.class, result);
        assertInstanceOf(TabulatedFunction.class, result);
    }
}