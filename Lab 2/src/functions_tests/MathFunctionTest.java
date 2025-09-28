package functions_tests;

import functions.MathFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MathFunctionTest {

    @Test
    void andThenTest() {
        MathFunction g = (x) -> x + 1;                  // g = x+1
        MathFunction f = (x) -> Math.pow(x, 3);         // f = x**3
        MathFunction h = g.andThen(f);                         // h = (x+1) ** 3
        MathFunction k = f.andThen(g);                         // k = x**3 + 1
        assertEquals(f.apply(g.apply(5)), h.apply(5));   // f(g(x)) == h(x)
        assertEquals(g.apply(f.apply(0)), k.apply(0));   // g(f(x)) == k(x)
    }

    @Test
    void andThenDifficultTest() {
        MathFunction g = (x) -> Math.pow(x,2) + 1;      // g = x**2+1
        MathFunction f = (x) -> 3*x + 5;                // f = 3x + 5
        MathFunction h = g.andThen(f);                         // h = 3(x**2+1) + 5
        MathFunction k = f.andThen(g);                         // k = (3x+5)**2 + 1
        assertEquals(f.apply(g.apply(5)), h.apply(5));   // f(g(x)) == h(x)
        assertEquals(g.apply(f.apply(0)), k.apply(0));   // g(f(x)) == k(x)
    }

}