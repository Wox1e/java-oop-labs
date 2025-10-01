package functions_tests;

import functions.IdentityFunction;
import org.junit.*;
import static org.junit.Assert.*;

public class IdentityFunctionTest {
    IdentityFunction func = new IdentityFunction();
    @Test
    public void testApply(){
        assertEquals(5.0, func.apply(5), 0.001);
    }
}