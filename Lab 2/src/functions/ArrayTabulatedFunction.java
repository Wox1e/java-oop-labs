package functions;

import java.util.Arrays;

public class ArrayTabulatedFunction extends AbstractTabulatedFunction implements Removable, Insertable {
    protected double[] xValues;
    protected double[] yValues;

    public ArrayTabulatedFunction(double[] xValues, double[] yValues) {
        this.count = xValues.length;
        this.xValues = Arrays.copyOf(xValues, count);
        this.yValues = Arrays.copyOf(yValues, count);
    }

    public ArrayTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) {

        this.count = count;
        xValues = new double[count];
        yValues = new double[count];

        if (xFrom == xTo) {
            double y = source.apply(xFrom);

            for (int i = 0; i < count; i++) {
                xValues[i] = xFrom;
                yValues[i] = y;
            }

            return;
        }

        if (xFrom > xTo) {
            double tmp = xFrom;
            xFrom = xTo;
            xTo = tmp;
        }


        double interval = (xTo - xFrom) / (count - 1);      // (x_1 - x_0) / кол-во интервалов
        for (int i = 0; i < count; i++) {
            xValues[i] = xFrom + i * interval;
            yValues[i] = source.apply(xValues[i]);
        }


    }

    @Override
    public double getX(int index) {
        return xValues[index];
    }

    @Override
    public double getY(int index) {
        return yValues[index];
    }

    @Override
    public void setY(int index, double value) {
        yValues[index] = value;
    }

    @Override
    public int indexOfY(double y) {
        for (int i = 0; i < count; ++i) {
            if (getY(i) == y) {
                return i;
            }
        }
        return -1;
    }


    @Override
    public double leftBound() {
        return xValues[0];
    }

    @Override
    public double rightBound() {
        return xValues[count - 1];
    }

    @Override
    protected int floorIndexOfX(double x) {
        if (xValues[0] > x) return 0;

        for (int i = 1; i < count; ++i) {
            if (xValues[i] > x) return i - 1;
        }

        return count;
    }

    @Override
    protected double extrapolateLeft(double x) {
        if (count == 1) return getY(0);
        return this.interpolate(x, xValues[0], xValues[1], yValues[0], yValues[1]);
    }

    @Override
    protected double extrapolateRight(double x) {
        if (count == 1) return getY(0);
        return this.interpolate(x, xValues[count - 2], xValues[count - 1], yValues[count - 2], yValues[count - 1]);
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        if (count == 1) return getY(0);
        return this.interpolate(x, xValues[floorIndex], xValues[floorIndex + 1], yValues[floorIndex], yValues[floorIndex + 1]);
    }

    @Override
    public int indexOfX(double x) {
        for (int i = 0; i < count; ++i) {
            if (getX(i) == x) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void remove(int index) {
        if (index < 0 || index >= count) throw new IndexOutOfBoundsException();
        double[] newXValues = new double[count - 1];
        double[] newYValues = new double[count - 1];

        int j = 0;
        for (int i = 0; i < count; i++) {
            if (i != index) {
                newXValues[j] = xValues[i];
                newYValues[j] = yValues[i];
                ++j;
            }
        }

        xValues = newXValues;
        yValues = newYValues;
        --count;

    }

    @Override
    public void insert(double x, double y) {
        if (this.leftBound() <= x && x <= this.rightBound()){
            for(int i = 0;i<count;i++){
                if (this.xValues[i] == x) {
                    this.yValues[i] = y;
                    break;
                }
                if (this.xValues[i] > x){
                    double[] xVal = new double[count + 1];
                    double[] yVal = new double[count + 1];
                    System.arraycopy(xValues, 0, xVal, 0, i);
                    System.arraycopy(yValues, 0, yVal, 0, i);
                    xVal[i] = x;
                    yVal[i] = y;
                    System.arraycopy(xValues, i, xVal, i + 1, count - i);
                    System.arraycopy(yValues, i, yVal, i + 1, count - i);
                    this.xValues = xVal;
                    this.yValues = yVal;
                    this.count++;
                    break;
                }
            }
        }
        else if (x < this.leftBound()) {
            double[] xVal = new double[count + 1];
            double[] yVal = new double[count + 1];
            xVal[0] = x;
            yVal[0] = y;
            System.arraycopy(xValues, 0, xVal, 1, count);
            System.arraycopy(yValues, 0, yVal, 1, count);
            this.xValues = xVal;
            this.yValues = yVal;
            this.count++;
        } else {
            double[] xVal = new double[count + 1];
            double[] yVal = new double[count + 1];
            System.arraycopy(xValues, 0, xVal, 0, count);
            System.arraycopy(yValues, 0, yVal, 0, count);
            xVal[count] = x;
            yVal[count] = y;
            this.xValues = xVal;
            this.yValues = yVal;
            this.count++;
        }
    }
}
