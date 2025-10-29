package functions_tests;

import functions.factory.LinkedListTabulatedFunctionFactory;
import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LinkedListTabulatedFunctionFactoryTest {

    @Test
    void testCreateReturnsLinkedListTabulatedFunction() {
        LinkedListTabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};

        TabulatedFunction result = factory.create(xValues, yValues);

        assertInstanceOf(LinkedListTabulatedFunction.class, result);
        assertInstanceOf(TabulatedFunction.class, result);
    }
}