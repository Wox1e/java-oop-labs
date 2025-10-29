package io;

import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;

import java.io.*;

import static io.FunctionsIO.writeTabulatedFunction;

public class TabulatedFunctionFileOutputStream {
    static void main(String[] args) {
        double[] xValues = new double[]{0.1, 0.2, 0.3, 0.4, 0.5};
        double[] yValues = new double[]{0.1, 0.2, 0.3, 0.4, 0.5};

        TabulatedFunction array = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction linkedList = new LinkedListTabulatedFunction(xValues, yValues);

        try (BufferedOutputStream arrayStream = new BufferedOutputStream(new FileOutputStream("./output/array function.bin"));
             BufferedOutputStream linkedListStream = new BufferedOutputStream(new FileOutputStream("./output/linked list function.bin"))) {

            writeTabulatedFunction(arrayStream, array);
            writeTabulatedFunction(linkedListStream, linkedList);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}