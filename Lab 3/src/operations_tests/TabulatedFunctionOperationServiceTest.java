package operations_tests;

import functions.ArrayTabulatedFunction;
import functions.Point;
import functions.TabulatedFunction;
import operations.TabulatedFunctionOperationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedFunctionOperationServiceTest {

    @Test
    void testAsPoints() {
        double[] xVals = {0, 1, 2, 3, 4};
        double[] yVals = {5, 8, 10, 12, 95};
        TabulatedFunction func = new ArrayTabulatedFunction(xVals, yVals);
        Point[] points = TabulatedFunctionOperationService.asPoints(func);
        int i = 0;
        assertEquals(xVals.length, points.length);
        for(Point point: points){
            assertEquals(xVals[i], point.x);
            assertEquals(yVals[i], point.y);
            ++i;
        }
    }
}