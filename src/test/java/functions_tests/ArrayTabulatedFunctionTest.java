package functions;

import functions.ArrayTabulatedFunction;
import functions.Point;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class ArrayTabulatedFunctionTest {

    ArrayTabulatedFunction arrTabFunc1;

    public ArrayTabulatedFunctionTest(){
        double[] xVals = {0.5,  1.8,  2.2,  3.3,  4.16, 5.33,  6.941};
        double[] yVals = {2.01, 3.22, 5.23, 8.0,  3.2,  0.2,   -5.5};
        arrTabFunc1 = new ArrayTabulatedFunction(xVals, yVals);
    }

    @Test
    void getX() {
        assertEquals(0.5, arrTabFunc1.getX(0));
        assertEquals(1.8, arrTabFunc1.getX(1));
        assertEquals(5.33, arrTabFunc1.getX(5));
    }

    @Test
    void getY() {
        assertEquals(5.23, arrTabFunc1.getY(2));
        assertEquals(3.22, arrTabFunc1.getY(1));
        assertEquals(0.2, arrTabFunc1.getY(5));
    }

    @Test
    void setY() {
        for (int i = 0; i < arrTabFunc1.getCount(); i++) {
            double y = 1.22;
            int index = 2;
            arrTabFunc1.setY(index, y);
            assertEquals(y, arrTabFunc1.getY(index));
        }
    }

    @Test
    void indexOfY() {
        int index = 2;
        double y = 1.56;
        arrTabFunc1.setY(index, y);
        assertEquals(index, arrTabFunc1.indexOfY(y));
        assertEquals(-1, arrTabFunc1.indexOfY(-1468498.9849));
    }

    @Test
    void rightBound() {
        assertEquals(6.941, arrTabFunc1.rightBound());
    }

    @Test
    void leftBound() {
        assertEquals(0.5, arrTabFunc1.leftBound());
    }

    @Test
    void floorIndexOfX() {
        double[] xVals = {0.5,  1.8,  2.2,  3.3,  4.16, 5.33,  6.941};
        double[] yVals = {2.01, 3.22, 5.23, 8.0,  3.2,  0.2,   -5.5};
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(xVals, yVals);
        assertEquals(3, func.floorIndexOfX(4));
        assertEquals(0, func.floorIndexOfX(-4.22));
        assertEquals(func.getCount(), func.floorIndexOfX(48));
    }

    @Test
    void extrapolateLeft() {
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(x -> 5.2, 2.0, 3.0, 2);
        double result = function.extrapolateLeft(1.0);
        assertEquals(5.2, result, 1e-10);
    }

    @Test
    void extrapolateRight() {
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(x -> Math.pow(x,2), 0.0, 4.0, 5);
        double result = function.extrapolateRight(6.0);

        // y = x^2
        // y = 9 + (16-9)/(4-3) * (x - 3) = 9 + 7*(x-3)
        // x = 6: y = 9 + 7*(6-3) = 9 + 21 = 30
        assertEquals(30.0, result, 1e-10);
    }

    @Test
    void interpolate() {
        double x1 = 1.0;
        double expected1 = 2.01 + (3.22 - 2.01) / (1.8 - 0.5) * (1.0 - 0.5);
        double result1 = arrTabFunc1.interpolate(x1, 0);
        assertEquals(expected1, result1, 1e-3);
    }

    @Test
    void indexOfX() {
        double[] xVals = {0.5,  1.8};
        double[] yVals = {2.01, 3.22};
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(xVals, yVals);
        assertEquals(0, func.indexOfX(0.5));
        assertEquals(-1, func.indexOfX(0.22356));
    }

    @Test
    void remove(){
        double[] xVals = {0.5,  1.8};
        double[] yVals = {2.01, 3.22};
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(xVals, yVals);
        func.remove(0);
        double[] expectedX = {1.8};
        double[] expectedY = {3.22};
        assertArrayEquals(expectedY, func.yValues);
        assertArrayEquals(expectedX, func.xValues);

        func.remove(0);
        expectedY = new double[]{};
        expectedX = new double[]{};

        assertArrayEquals(expectedX, func.xValues);
        assertArrayEquals(expectedY, func.yValues);

    }

    @Test
    void insert(){
        double[] xVals = {0.5,  1.8};
        double[] yVals = {2.01, 3.22};
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(xVals, yVals);
        func.insert(0, 0.6);
        assertEquals(0.0, func.leftBound());
        func.insert(0.5, 1.5);
        assertEquals(1.5, func.apply(0.5));
        func.insert(6.2, 0.99);
        assertEquals(6.2, func.rightBound());
        func.insert(0.7, 1.999);
        assertEquals(1.999, func.apply(0.7));
    }

    @Test
    public void ConstructorTest(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()->new ArrayTabulatedFunction(new double[]{1}, new double[]{1}));
        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, ()->new ArrayTabulatedFunction(y->1, 1, 3, 1));
    }

    @Test
    public void iteratorTest(){
        double[] xVals = {0.41,  1.224, 5.56};
        double[] yVals = {2.79, 4.252, 0.85};
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(xVals, yVals);
        Iterator<Point> iterator = func.iterator();
        int i = 0;
        while(iterator.hasNext()) {
            Point point = iterator.next();
            assertEquals(point.x, xVals[i]);
            assertEquals(point.y, yVals[i]);
            ++i;
        }

        assertThrows(RuntimeException.class, () -> {iterator.next();});

        i = 0;
        for (Point point : func) {
            assertEquals(point.x, xVals[i]);
            assertEquals(point.y, yVals[i]);
            ++i;
        }



    }


}