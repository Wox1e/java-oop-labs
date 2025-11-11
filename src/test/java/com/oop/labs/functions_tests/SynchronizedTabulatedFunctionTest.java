package com.oop.labs.functions_tests;

import com.oop.labs.concurrent.SynchronizedTabulatedFunction;
import com.oop.labs.functions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class SynchronizedTabulatedFunctionTest {

    private TabulatedFunction baseFunction;
    private SynchronizedTabulatedFunction syncFunction;

    @BeforeEach
    void setUp() {
        baseFunction = new LinkedListTabulatedFunction(new double[]{1.0, 2.0, 3.0, 4.0}, new double[]{10.0, 20.0, 30.0, 40.0});
        syncFunction = new SynchronizedTabulatedFunction(baseFunction);
    }

    @Test
    void testDoSync(){
        double syncResult = syncFunction.doSynchronously(SynchronizedTabulatedFunction::getCount);
        double result = baseFunction.getCount();
        assertEquals(result, syncResult);

        var nillResult = syncFunction.doSynchronously(new SynchronizedTabulatedFunction.Operation<Object>() {
                                         @Override
                                         public Object apply(SynchronizedTabulatedFunction syncFunc) {
                                             return null;
                                         }
                                     });
        assertNull(nillResult);

        double multiFunc = (double) syncFunction.doSynchronously(new SynchronizedTabulatedFunction.Operation<Object>() {
            @Override
            public Object apply(SynchronizedTabulatedFunction syncFunc) {
                syncFunc.setY(0, 52.68);
                return syncFunc.getY(0);
            }
        });

        assertEquals(52.68, multiFunc);
    }

    @Test
    void testGetCount() {
        assertEquals(4, syncFunction.getCount());
    }

    @Test
    void testGetX() {
        assertEquals(1.0, syncFunction.getX(0), 1e-9);
        assertEquals(2.0, syncFunction.getX(1), 1e-9);
        assertEquals(3.0, syncFunction.getX(2), 1e-9);
        assertEquals(4.0, syncFunction.getX(3), 1e-9);
    }

    @Test
    void testGetXWithInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> syncFunction.getX(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> syncFunction.getX(10));
    }

    @Test
    void testGetY() {
        assertEquals(10.0, syncFunction.getY(0), 1e-9);
        assertEquals(20.0, syncFunction.getY(1), 1e-9);
        assertEquals(30.0, syncFunction.getY(2), 1e-9);
        assertEquals(40.0, syncFunction.getY(3), 1e-9);
    }

    @Test
    void testGetYWithInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> syncFunction.getY(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> syncFunction.getY(10));
    }

    @Test
    void testSetY() {
        syncFunction.setY(0, 100.0);
        assertEquals(100.0, syncFunction.getY(0), 1e-9);
        assertEquals(100.0, baseFunction.getY(0), 1e-9); // Проверяем изменение в базовой функции

        syncFunction.setY(2, 200.0);
        assertEquals(200.0, syncFunction.getY(2), 1e-9);
        assertEquals(200.0, baseFunction.getY(2), 1e-9);
    }

    @Test
    void testSetYWithInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> syncFunction.setY(-1, 100.0));
        assertThrows(IndexOutOfBoundsException.class, () -> syncFunction.setY(10, 100.0));
    }

    @Test
    void testApply() {
        assertEquals(10.0, syncFunction.apply(1.0), 1e-9);
        assertEquals(20.0, syncFunction.apply(2.0), 1e-9);
        assertEquals(30.0, syncFunction.apply(3.0), 1e-9);
        assertEquals(40.0, syncFunction.apply(4.0), 1e-9);
    }

    @Test
    void testApplyWithInterpolation() {
        // Для значения между точками (интерполяция)
        double result = syncFunction.apply(1.5);
        assertTrue(result > 10.0 && result < 20.0);
    }

    @Test
    void testApplyWithExtrapolation() {
        assertEquals(0.0, syncFunction.apply(0.0), 1e-9);
        assertEquals(50.0, syncFunction.apply(5.0), 1e-9);
        assertEquals(-100.0, syncFunction.apply(-10.0), 1e-9);
        assertEquals(1000.0, syncFunction.apply(100.0), 1e-9);
    }

    @Test
    void testIndexOfX() {
        assertEquals(0, syncFunction.indexOfX(1.0));
        assertEquals(1, syncFunction.indexOfX(2.0));
        assertEquals(2, syncFunction.indexOfX(3.0));
        assertEquals(3, syncFunction.indexOfX(4.0));
        assertEquals(-1, syncFunction.indexOfX(5.0)); // Несуществующее значение
    }

    @Test
    void testIndexOfY() {
        assertEquals(0, syncFunction.indexOfY(10.0));
        assertEquals(1, syncFunction.indexOfY(20.0));
        assertEquals(2, syncFunction.indexOfY(30.0));
        assertEquals(3, syncFunction.indexOfY(40.0));
        assertEquals(-1, syncFunction.indexOfY(50.0)); // Несуществующее значение
    }

    @Test
    void testLeftBound() {
        assertEquals(1.0, syncFunction.leftBound(), 1e-9);
    }

    @Test
    void testRightBound() {
        assertEquals(4.0, syncFunction.rightBound(), 1e-9);
    }

    @Test
    void testIterator() {
        Iterator<Point> iterator = syncFunction.iterator();

        assertTrue(iterator.hasNext());
        Point point1 = iterator.next();
        assertEquals(1.0, point1.x, 1e-9);
        assertEquals(10.0, point1.y, 1e-9);

        assertTrue(iterator.hasNext());
        Point point2 = iterator.next();
        assertEquals(2.0, point2.x, 1e-9);
        assertEquals(20.0, point2.y, 1e-9);

        assertTrue(iterator.hasNext());
        Point point3 = iterator.next();
        assertEquals(3.0, point3.x, 1e-9);
        assertEquals(30.0, point3.y, 1e-9);

        assertTrue(iterator.hasNext());
        Point point4 = iterator.next();
        assertEquals(4.0, point4.x, 1e-9);
        assertEquals(40.0, point4.y, 1e-9);

        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void testIteratorForEachLoop() {
        int count = 0;
        double expectedX = 1.0;
        double expectedY = 10.0;

        for (Point point : syncFunction) {
            assertEquals(expectedX, point.x, 1e-9);
            assertEquals(expectedY, point.y, 1e-9);
            expectedX += 1.0;
            expectedY += 10.0;
            count++;
        }

        assertEquals(4, count);
    }

    @Test
    void testMultipleIterators() {
        Iterator<Point> iterator1 = syncFunction.iterator();
        Iterator<Point> iterator2 = syncFunction.iterator();

        // Оба итератора должны работать независимо
        Point point1 = iterator1.next();
        Point point2 = iterator2.next();

        assertEquals(1.0, point1.x, 1e-9);
        assertEquals(1.0, point2.x, 1e-9);
        assertEquals(10.0, point1.y, 1e-9);
        assertEquals(10.0, point2.y, 1e-9);
    }

    @Test
    void testWithArrayTabulatedFunction() {
        // Тестируем с другой реализацией TabulatedFunction
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{5.0, 6.0}, new double[]{50.0, 60.0});
        SynchronizedTabulatedFunction syncArrayFunction = new SynchronizedTabulatedFunction(arrayFunction);

        assertEquals(2, syncArrayFunction.getCount());
        assertEquals(5.0, syncArrayFunction.getX(0), 1e-9);
        assertEquals(50.0, syncArrayFunction.getY(0), 1e-9);
        assertEquals(60.0, syncArrayFunction.apply(6.0), 1e-9);
    }

    @Test
    void testWithConstantFunction() {
        // Тестируем с функцией созданной на основе ConstantFunction
        ConstantFunction constant = new ConstantFunction(5.0);
        TabulatedFunction tabulatedFromConstant = new LinkedListTabulatedFunction(constant, 0.0, 10.0, 5);
        SynchronizedTabulatedFunction syncFromConstant = new SynchronizedTabulatedFunction(tabulatedFromConstant);

        assertEquals(5, syncFromConstant.getCount());
        for (int i = 0; i < syncFromConstant.getCount(); i++) {
            assertEquals(5.0, syncFromConstant.getY(i), 1e-9);
        }
    }

    @Test
    void testDataConsistency() {
        // Проверяем согласованность данных после множества операций
        for (int i = 0; i < syncFunction.getCount(); i++) {
            double newValue = i * 100.0;
            syncFunction.setY(i, newValue);
            assertEquals(newValue, syncFunction.getY(i), 1e-9);
            assertEquals(newValue, baseFunction.getY(i), 1e-9);
        }

        // Проверяем apply после изменений
        for (int i = 0; i < syncFunction.getCount(); i++) {
            double x = syncFunction.getX(i);
            double expectedY = i * 100.0;
            assertEquals(expectedY, syncFunction.apply(x), 1e-9);
        }
    }
}