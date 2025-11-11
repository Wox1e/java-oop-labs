package com.oop.labs.functions;

import com.oop.labs.exceptions.ArrayIsNotSortedException;
import com.oop.labs.exceptions.DifferentLengthOfArraysException;

public abstract class AbstractTabulatedFunction implements MathFunction, TabulatedFunction {

    protected int count;

    public abstract double getX(int index);
    public abstract double getY(int index);

    protected abstract int floorIndexOfX(double x);
    protected abstract double extrapolateLeft(double x);
    protected abstract double extrapolateRight(double x);
    protected abstract double interpolate(double x, int floorIndex);

    protected double interpolate(double x, double leftX, double rightX, double leftY, double rightY) {
        return leftY + ((rightY - leftY) / (rightX - leftX)) * (x - leftX);
    }

    public int getCount() {
        return count;
    }

    abstract public int indexOfX(double x);

    static public void checkLengthIsTheSame(double[] xValues, double[] yValues){
        if(xValues.length != yValues.length) throw new DifferentLengthOfArraysException();
    }

    static public void checkSorted(double[] xValues){
        for (int i = 1; i < xValues.length; i++) {
            if (xValues[i] < xValues[i-1]) throw new ArrayIsNotSortedException();
        }
    }

    @Override
    public double apply(double x) {

        // Если x вне таблицы (методы эктраполяции)
        if (x > getX(count - 1)) {
            return extrapolateRight(x); // Правая экстраполяция
        } else if (x < getX(0)) {
            return extrapolateLeft(x);  // Левая экстраполяция
        }
        //


        int index = indexOfX(x);        //
        if (indexOfX(x) != -1) {        //
            return getY(index);         // Для этого x есть значение y в таблице
        }                               //

        // floorIndex - ближайший слева x
        return interpolate(x, floorIndexOfX(x));
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(getClass().getSimpleName())
                .append(" size = ")
                .append(count)
                .append("\n");
        for(Point point : this) {
            str.append("[")
                    .append(point.x)
                    .append("; ")
                    .append(point.y)
                    .append("]\n");
        }
        return str.toString();
    }
}