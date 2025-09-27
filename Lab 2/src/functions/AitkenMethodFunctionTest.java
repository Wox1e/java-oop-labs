package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AitkenMethodFunctionTest {

    @Test
    void applyBasicTest() {
        MathFunction slowFunc = x -> (x + 2/x) / 2;
        AitkenMethodFunction aitken = new AitkenMethodFunction(slowFunc);

        assertEquals(1.4142, aitken.apply(1.0), 0.001);
    }

    @Test
    void applyWithSimpleFunctionsTest() {
        IdentityFunction id = new IdentityFunction();
        AitkenMethodFunction aitkenId = new AitkenMethodFunction(id);

        assertEquals(5.0, aitkenId.apply(5.0));
        assertEquals(-2.0, aitkenId.apply(-2.0));
    }
}