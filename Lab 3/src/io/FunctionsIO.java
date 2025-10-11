package io;

import functions.Point;
import functions.TabulatedFunction;

import java.io.BufferedWriter;
import java.io.PrintWriter;

public final class FunctionsIO {
    private FunctionsIO() {
        throw new UnsupportedOperationException();
    }
    static void writeTabulatedFunction(BufferedWriter writer, TabulatedFunction function){
        PrintWriter printWriter = new PrintWriter(writer);

        int count = function.getCount();
        printWriter.println(count);
        for (Point point:function){
            printWriter.printf("%f %f\n", point.x, point.y);
        }
        printWriter.flush();
    }
}
