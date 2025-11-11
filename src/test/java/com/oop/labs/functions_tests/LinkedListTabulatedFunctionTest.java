package com.oop.labs.functions_tests;

import com.oop.labs.functions.LinkedListTabulatedFunction;
import com.oop.labs.functions.Point;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

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
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, ()->lst.getX(-1));
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
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()->lst.floorIndexOfX(-999));
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

        // ЗАМЕНИТЬ тест для одной точки на тест для двух точек
        double[] twoX = {2.0, 3.0};
        double[] twoY = {3.0, 4.0};
        LinkedListTabulatedFunction twoPointFunc = new LinkedListTabulatedFunction(twoX, twoY);

        // Экстраполяция слева от первой точки
        assertEquals(2.0, twoPointFunc.extrapolateLeft(1.0), 1e-9);
        assertEquals(1.0, twoPointFunc.extrapolateLeft(0.0), 1e-9);
    }

    @Test
    public void extrapolateRight() {
        double expected = 7.0;
        assertEquals(expected, lst.extrapolateRight(7.0), 1e-9);
        assertEquals(8.0, lst.extrapolateRight(8.0), 1e-9);

        // ЗАМЕНИТЬ тест для одной точки на тест для двух точек
        double[] twoX = {2.0, 3.0};
        double[] twoY = {3.0, 4.0};
        LinkedListTabulatedFunction twoPointFunc = new LinkedListTabulatedFunction(twoX, twoY);

        // Экстраполяция справа от последней точки
        assertEquals(5.0, twoPointFunc.extrapolateRight(4.0), 1e-9);
        assertEquals(6.0, twoPointFunc.extrapolateRight(5.0), 1e-9);
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
    public void insert() {

        LinkedListTabulatedFunction lst = new LinkedListTabulatedFunction(
                new double[]{1.0, 2.0},
                new double[]{10.0, 20.0}
        );

        // Вставка справа
        lst.insert(3.0, 30.0);
        assertEquals(3.0, lst.rightBound(), 1e-9);
        assertEquals(30.0, lst.apply(3.0), 1e-9);
        assertEquals(3, lst.getCount());

        // Вставка слева
        lst.insert(0.5, 5.0);
        assertEquals(0.5, lst.leftBound(), 1e-9);
        assertEquals(5.0, lst.apply(0.5), 1e-9);
        assertEquals(4, lst.getCount());

        // Вставка в середину
        lst.insert(1.5, 15.0);
        assertEquals(15.0, lst.apply(1.5), 1e-9);
        assertEquals(5, lst.getCount());

        // Вставка существующей точки
        lst.insert(1.5, 150.0);
        assertEquals(150.0, lst.apply(1.5), 1e-9);
        assertEquals(5, lst.getCount());
    }

    @Test
    void remove(){
        lst.remove(2);
        assertEquals(4, lst.getY(2));
        lst.remove(0);
        assertEquals(2, lst.getY(0));
        double[] xVal = new double[]{1, 2};
        double[] yVal = new double[]{1, 2};
        LinkedListTabulatedFunction test_lst = new LinkedListTabulatedFunction(xVal, yVal);
        test_lst.remove(0);
        assertEquals(1, test_lst.getCount());
    }

    @Test
    public void testConstructorWithSinglePoint() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new LinkedListTabulatedFunction(new double[]{1.0}, new double[]{2.0}));
        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class,
                () -> new LinkedListTabulatedFunction(y->1, 1, 2, 1));
    }

    @Test
    void testIterator() {
        Iterator<Point> iterator = lst.iterator();  // Правильно!

        int i = -1;
        while (iterator.hasNext()) {
            Point point = iterator.next();
            i++;
            assertEquals(point.x, x_arr[i]);
            assertEquals(point.y, y_arr[i]);
        }

        i = -1;
        for (Point point : lst){
            i++;
            assertEquals(point.x, x_arr[i]);
            assertEquals(point.y, y_arr[i]);
        }
    }
}