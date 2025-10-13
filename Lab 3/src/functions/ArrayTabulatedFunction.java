package functions;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

public class ArrayTabulatedFunction extends AbstractTabulatedFunction implements Removable, Insertable, Serializable {
    protected double[] xValues;
    protected double[] yValues;

    @Serial
    private static final long serialVersionUID = -6025850912561369097L;

    public ArrayTabulatedFunction(double[] xValues, double[] yValues) throws IllegalArgumentException{
        checkLengthIsTheSame(xValues, yValues);
        checkSorted(xValues);
        if (xValues.length < 2) throw new IllegalArgumentException("lower than 2 strings");
        this.count = xValues.length;
        this.xValues = Arrays.copyOf(xValues, count);
        this.yValues = Arrays.copyOf(yValues, count);
    }

    public ArrayTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) throws IllegalArgumentException{
        if (count < 2) throw new IllegalArgumentException("lower than 2 strings");
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
        double[] newXValues = new double[count + 1];
        double[] newYValues = new double[count + 1];
        int insertPosition = 0;

        if (this.leftBound() <= x && x <= this.rightBound()) {
            for (int i = 0; i < count; i++) {
                if (this.xValues[i] == x) {
                    this.yValues[i] = y;
                    return;
                }
                if (this.xValues[i] > x) {
                    insertPosition = i;
                    break;
                }
            }
        } else if (x < this.leftBound()) {
            insertPosition = 0;
        } else {
            insertPosition = count;
        }

        System.arraycopy(xValues, 0, newXValues, 0, insertPosition);
        System.arraycopy(yValues, 0, newYValues, 0, insertPosition);

        newXValues[insertPosition] = x;
        newYValues[insertPosition] = y;

        System.arraycopy(xValues, insertPosition, newXValues, insertPosition + 1, count - insertPosition);
        System.arraycopy(yValues, insertPosition, newYValues, insertPosition + 1, count - insertPosition);

        this.xValues = newXValues;
        this.yValues = newYValues;
        this.count++;
    }

    @Override
    public Iterator<Point> iterator() {
        return new Iterator<Point>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < count;
            }

            @Override
            public Point next() {
                if(!hasNext()) throw new IndexOutOfBoundsException("No next element");
                Point point = new Point(getX(index), getY(index));
                ++index;
                return point;
            }
        };
    }}
