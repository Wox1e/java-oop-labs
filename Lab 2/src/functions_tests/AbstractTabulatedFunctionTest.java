package functions_tests;

import org.junit.jupiter.api.Test;

import functions.AbstractTabulatedFunction;
import static org.junit.jupiter.api.Assertions.*;


class MockTabulatedFunction extends AbstractTabulatedFunction {

    public void mockSetCount(int count){
        this.count = count;
    }

    @Override
    public double getX(int index) {
        return 0;
    }

    @Override
    public double getY(int index) {
        return 0;
    }

    @Override
    protected int floorIndexOfX(double index) {
        return 0;
    }

    @Override
    protected double extrapolateLeft(double x) {
        return 0;
    }

    @Override
    protected double extrapolateRight(double x) {
        return 0;
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        return 0;
    }

    public double interpolate(double x, double leftX, double rightX, double leftY, double rightY) {
        return super.interpolate(x, leftX, rightX, leftY, rightY);
    }

    @Override
    public void setY(int index, double value) {

    }

    @Override
    public int indexOfY(double y) {
        return 0;
    }

    @Override
    public double leftBound() {
        return 0;
    }

    @Override
    public double rightBound() {
        return 0;
    }
}

class AbstractTabulatedFunctionTest {
    private final MockTabulatedFunction mock;

    public AbstractTabulatedFunctionTest(){
         mock = new MockTabulatedFunction();
         mock.mockSetCount(15);
    }

    @Test
    void testGetCount() {
        assertEquals(15, mock.getCount());
    }


    @Test
    public void testInterpolateMiddlePoint() {
        double result = mock.interpolate(5.0, 0.0, 10.0, 0.0, 20.0);
        assertEquals(10.0, result, 0.001);
    }

    @Test
    public void testInterpolateAtLeftBoundary() {
        double result = mock.interpolate(0.0, 0.0, 10.0, 5.0, 15.0);
        assertEquals(5.0, result, 0.001);
    }

    @Test
    public void testInterpolateAtRightBoundary() {
        double result = mock.interpolate(10.0, 0.0, 10.0, 5.0, 15.0);
        assertEquals(15.0, result, 0.001);
    }
}