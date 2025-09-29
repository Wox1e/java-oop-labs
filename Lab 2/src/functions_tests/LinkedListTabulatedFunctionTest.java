package functions_tests;

import functions.LinkedListTabulatedFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LinkedListTabulatedFunctionTest {
    double[] x_arr = new double[]{1, 2, 3, 4, 5, 6};
    double[] y_arr = new double[]{1, 2, 3, 4, 5, 6};
    LinkedListTabulatedFunction lst = new LinkedListTabulatedFunction(x_arr, y_arr);

    @Test
    public void addNode(){
        lst.addNode(7.0, 2.2);
        assertEquals(7.0, lst.getX(6));
        assertEquals(2.2, lst.getY(6));
    }

    @Test
    public void getX() {
        assertEquals(2, lst.getX(1));
    }

    @Test
    public void getY() {
        assertEquals(2, lst.getX(1));
    }

    @Test
    public void setY(){
        lst.setY(0, 0);
        assertEquals(0, lst.getY(0));
    }

    @Test
    public void indexOfY(){
        assertEquals(2, lst.indexOfY(3));
    }

    @Test
    public void indexOfX(){
        assertEquals(2, lst.indexOfY(3));
    }

    @Test
    public void floorIndexOfX(){
        assertEquals(1, lst.floorIndexOfX(2.5));
    }

    @Test
    public void interpolate(){
        double expected = 2.5;
        assertEquals(expected, lst.interpolate(2.5, 1), 1e-9);
    }

    @Test
    public void extrapolateLeft() {
        double expected = 0.5;
        assertEquals(expected, lst.extrapolateLeft(0.5), 1e-9);

        assertEquals(-1.0, lst.extrapolateLeft(-1.0), 1e-9);

        // Тест для одной точки
        double[] singleX = {2.0};
        double[] singleY = {3.0};
        LinkedListTabulatedFunction singleFunc = new LinkedListTabulatedFunction(singleX, singleY);
        assertEquals(3.0, singleFunc.extrapolateLeft(1.0), 1e-9);
        assertEquals(3.0, singleFunc.extrapolateLeft(5.0), 1e-9);
    }

    @Test
    public void extrapolateRight() {
        double expected = 7.0;
        assertEquals(expected, lst.extrapolateRight(7.0), 1e-9);

        assertEquals(8.0, lst.extrapolateRight(8.0), 1e-9);

        // Тест для одной точки
        double[] singleX = {2.0};
        double[] singleY = {3.0};
        LinkedListTabulatedFunction singleFunc = new LinkedListTabulatedFunction(singleX, singleY);
        assertEquals(3.0, singleFunc.extrapolateRight(3.0), 1e-9);
        assertEquals(3.0, singleFunc.extrapolateRight(10.0), 1e-9);
    }

    @Test
    public void rightBound(){
        assertEquals(6, lst.rightBound());
    }

    @Test
    public void leftBound(){
        assertEquals(1, lst.leftBound());
    }

    @Test
    public void insertTest(){
        LinkedListTabulatedFunction emptyLst = new LinkedListTabulatedFunction(new double[]{}, new double[]{});
        emptyLst.insert(6,1);
        assertEquals(6, emptyLst.rightBound());
        assertEquals(1, emptyLst.apply(6));
        assertEquals(1, emptyLst.getCount());

        lst.insert(5, 0);
        assertEquals(0, lst.apply(5));

        lst.insert(666.064, 125.664);
        assertEquals(666.064, lst.rightBound());

        lst.insert(5.06, 7.023);
        assertEquals(7.023, lst.apply(5.06));

        lst.insert(0.001, 86);
        assertEquals(0.001, lst.leftBound());
        assertEquals(86, lst.apply(0.001));

    }

}