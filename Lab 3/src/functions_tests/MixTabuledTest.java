package functions_tests;

import functions.ArrayTabulatedFunction;
import functions.ConstantFunction;
import functions.LinkedListTabulatedFunction;
import functions.MathFunction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class MixTabuledTest {

    @Test
    void twoTabuledTest(){
        ArrayTabulatedFunction arrayTabuled = new ArrayTabulatedFunction(new double[]{1,2,3,4,5}, new double[]{1,4,9,16,25}); // y = x**2
        LinkedListTabulatedFunction linkedListTabuled = new LinkedListTabulatedFunction(new double[]{1,2,3,4,5}, new double[]{2,3,4,5,6}); // y = x+x
        double res = linkedListTabuled.andThen(arrayTabuled).apply(4); // y = (x+1)**2
        double expected = 25;
        assertEquals(expected, res);
    }

    @Test
    void tabuledAndConstTest(){
        MathFunction func = (x) -> x+5;
        ArrayTabulatedFunction arrayTabuled = new ArrayTabulatedFunction(func, 1, 16, 5);   // y = x+5
        ConstantFunction constFunc = new ConstantFunction(16);
        double res =  constFunc.andThen(arrayTabuled).apply(2);
        double expected = 21;
        assertEquals(expected, res);
    }

}
