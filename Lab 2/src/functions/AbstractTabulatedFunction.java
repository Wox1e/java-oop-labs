package functions;

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

    public int indexOfX(double x) {
        for (int i = 0; i < count; ++i) {
            if (getX(i) == x) {
                return i;
            }
        }
        return -1;
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


}