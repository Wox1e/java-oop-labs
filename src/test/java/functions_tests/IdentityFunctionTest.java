package functions_tests;

import functions.IdentityFunction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IdentityFunctionTest {
    IdentityFunction func = new IdentityFunction();
    @Test
    public void testApply(){
        assertEquals(5.0, func.apply(5), 0.001);
    }
}