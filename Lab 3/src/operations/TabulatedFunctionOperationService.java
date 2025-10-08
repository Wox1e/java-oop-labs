package operations;

import functions.Point;
import functions.TabulatedFunction;

import java.util.Iterator;

public class TabulatedFunctionOperationService {
    public static Point[] asPoints(TabulatedFunction tabulatedFunction){
        Point[] pointArray = new Point[tabulatedFunction.getCount()];
        int i = 0;
        for (Point point:tabulatedFunction){
            pointArray[i++] = point;
        }
        return pointArray;
    }
}
