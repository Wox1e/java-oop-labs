package io;

import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;
import operations.TabulatedDifferentialOperator;

import java.io.*;

public class TabulatedFunctionFileInputStream {
    static void main(String[] args) {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("input/binary function.bin"))) {
            TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
            TabulatedFunction function = FunctionsIO.readTabulatedFunction(bufferedInputStream, factory);
            System.out.println(function.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Введите размер и значения функции:");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            TabulatedFunctionFactory factory1 = new LinkedListTabulatedFunctionFactory();
            TabulatedFunction function1 = FunctionsIO.readTabulatedFunction(reader, factory1);
            TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();
            TabulatedFunction derivative = operator.derive(function1);
            System.out.println("Производная функции:");
            System.out.println(derivative.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
