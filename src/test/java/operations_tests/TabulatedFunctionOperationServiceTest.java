package operations_tests;

import concurrent.SynchronizedTabulatedFunction;
import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.Point;
import functions.TabulatedFunction;
import operations.TabulatedDifferentialOperator;
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


    @Test
    void testAdd() {
        double[] xVals = {1,2,3,4};
        double[] yVals = {15, 36, 9, 0};
        double[] yVals2 = {1.55, 5.36, 9.58, 50};
        double[] yVals3 = {-363.6, 52.76, -0.52, 0.03};

        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xVals, yVals);

        ArrayTabulatedFunction func2 = new ArrayTabulatedFunction(xVals, yVals2);

        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();
        TabulatedFunction newFunc = service.add(func, func2);

        assertEquals(newFunc.apply(0), (func.apply(0) + func2.apply(0)), 10e-3);
        assertEquals(newFunc.apply(5), (func.apply(5) + func2.apply(5)), 10e-3);

        ArrayTabulatedFunction func3 = new ArrayTabulatedFunction(xVals, yVals3);

        TabulatedFunction newFunc2 = service.add(newFunc, func3);

        assertEquals(newFunc2.apply(0), (func.apply(0) + func2.apply(0) + func3.apply(0)), 10e-3);
        assertEquals(newFunc2.apply(5), (func.apply(5) + func2.apply(5) + func3.apply(5)), 10e-3);
    }

    @Test
    void testSub() {
        double[] xVals = {1,2,3,4};
        double[] yVals = {15, 36, 9, 0};
        double[] yVals2 = {1.55, 5.36, 9.58, 50};
        double[] yVals3 = {-363.6, 52.76, -0.52, 0.03};

        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xVals, yVals);

        ArrayTabulatedFunction func2 = new ArrayTabulatedFunction(xVals, yVals2);

        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();
        TabulatedFunction newFunc = service.sub(func, func2);

        assertEquals(newFunc.apply(0), (func.apply(0) - func2.apply(0)), 10e-3);
        assertEquals(newFunc.apply(5), (func.apply(5) - func2.apply(5)), 10e-3);

        ArrayTabulatedFunction func3 = new ArrayTabulatedFunction(xVals, yVals3);

        TabulatedFunction newFunc2 = service.sub(newFunc, func3);

        assertEquals(newFunc2.apply(0), ((func.apply(0) - func2.apply(0)) - func3.apply(0)), 10e-3);
        assertEquals(newFunc2.apply(5), ((func.apply(5) - func2.apply(5)) - func3.apply(5)), 10e-3);
    }


    @Test
    void testSyncDerive(){
        double[] xVals = {1,2,3,4};
        double[] yVals = {2, 4, 6, 8};
        TabulatedFunction func = new ArrayTabulatedFunction(xVals, yVals);
        SynchronizedTabulatedFunction synchronizedFunc = new SynchronizedTabulatedFunction(func);
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        TabulatedFunction syncDerivedFunction = operator.deriveSynchronously(synchronizedFunc);
        TabulatedFunction derivedFunction = operator.derive(synchronizedFunc);

        for (int i = 0; i < syncDerivedFunction.getCount() - 1; i++) {
            assertEquals(derivedFunction.getY(i), syncDerivedFunction.getY(i));
            assertEquals(derivedFunction.getX(i), syncDerivedFunction.getX(i));

        }



    }

}