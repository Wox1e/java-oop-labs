package operations;

import exceptions.InconsistentFunctionsException;
import functions.Point;
import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;

public class TabulatedFunctionOperationService {

    private TabulatedFunctionFactory factory;

    public TabulatedFunctionOperationService(TabulatedFunctionFactory factory){
        this.factory = factory;
    }

    public TabulatedFunctionOperationService() {
        this.factory = new ArrayTabulatedFunctionFactory();
    }

    public TabulatedFunctionFactory getFactory() {
        return factory;
    }

    public void setFactory(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }


    public static Point[] asPoints(TabulatedFunction tabulatedFunction){
        Point[] pointArray = new Point[tabulatedFunction.getCount()];
        int i = 0;
        for (Point point:tabulatedFunction){
            pointArray[i++] = point;
        }
        return pointArray;
    }

    private interface BiOperation {
        double apply(double u, double v);
    }


    private TabulatedFunction doOperation(TabulatedFunction a, TabulatedFunction b, BiOperation binOperation) {
        
        if (a.getCount() != b.getCount()) throw new InconsistentFunctionsException();

        Point[] aPoints = asPoints(a);
        Point[] bPoints = asPoints(b);

        int pointsCount = aPoints.length;

        double[] xValues = new double[pointsCount];
        double[] yValues = new double[pointsCount];

        for (int i = 0; i < pointsCount; ++i) {
            if (aPoints[i].x != bPoints[i].x) throw new InconsistentFunctionsException();
            xValues[i] = aPoints[i].x;
            yValues[i] = binOperation.apply(aPoints[i].y, bPoints[i].y);
        }

        return factory.create(xValues, yValues);
    }

    public TabulatedFunction add(TabulatedFunction a, TabulatedFunction b) {
        return doOperation(a, b, (x, y) -> x + y);
    }

    public TabulatedFunction sub(TabulatedFunction a, TabulatedFunction b) {
        return doOperation(a, b, (x, y) -> x - y);
    }

    public TabulatedFunction div(TabulatedFunction a, TabulatedFunction b) {
        return doOperation(a, b, (x, y) -> x / y);
    }

    public TabulatedFunction mul(TabulatedFunction a, TabulatedFunction b) {
        return doOperation(a, b, (x, y) -> x * y);
    }
    
}
