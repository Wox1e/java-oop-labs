package functions_tests;

import functions.CompositeFunction;
import functions.ConstantFunction;
import functions.IdentityFunction;
import functions.SqrFunction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompositeFunctionTest {

    @Test
    void applyWithIdentityAndSqrTest() {
        IdentityFunction first = new IdentityFunction();
        SqrFunction second = new SqrFunction();
        CompositeFunction composite = new CompositeFunction(first, second);

        // f(x) = x, g(x) = x², тогда g(f(x)) = x²
        assertEquals(25.0, composite.apply(5.0));
        assertEquals(9.0, composite.apply(3.0));
    }

    @Test
    void applyWithSqrAndIdentityTest() {
        SqrFunction first = new SqrFunction();
        IdentityFunction second = new IdentityFunction();
        CompositeFunction composite = new CompositeFunction(first, second);

        // f(x) = x², g(x) = x, тогда g(f(x)) = x²
        assertEquals(16.0, composite.apply(4.0));
        assertEquals(4.0, composite.apply(2.0));
    }

    @Test
    void applyWithConstantTest() {
        ConstantFunction first = new ConstantFunction(5.0);
        SqrFunction second = new SqrFunction();
        CompositeFunction composite = new CompositeFunction(first, second);

        // f(x) = 5, g(x) = x², тогда g(f(x)) = 25
        assertEquals(25.0, composite.apply(10.0));  // любой x даст 25
        assertEquals(25.0, composite.apply(0.0));
    }

    @Test
    void applyCompositeOfCompositesTest() {
        // h(x) = g(f(x)), где f и g сами являются сложными функциями
        IdentityFunction id = new IdentityFunction();
        SqrFunction sqr = new SqrFunction();

        CompositeFunction f = new CompositeFunction(id, sqr);  // f(x) = x²
        CompositeFunction g = new CompositeFunction(sqr, id);  // g(x) = x²

        CompositeFunction h = new CompositeFunction(f, g);  // h(x) = g(f(x)) = (x²)² = x⁴

        assertEquals(16.0, h.apply(2.0));   // 2⁴ = 16
        assertEquals(81.0, h.apply(3.0));   // 3⁴ = 81
    }
}