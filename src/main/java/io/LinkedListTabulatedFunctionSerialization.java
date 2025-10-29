package io;

import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;
import operations.TabulatedDifferentialOperator;

import java.io.*;

public class LinkedListTabulatedFunctionSerialization {
    static void main(String[] args) {
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                new FileOutputStream("output/serialized linked list functions.bin"))) {

            double[] xValues = new double[] {0.1, 0.2, 0.3};
            double[] yValues = new double[] {1, 2, 3};
            TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

            TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();
            TabulatedFunction diffFunc1 = operator.derive(function);
            TabulatedFunction diffFunc2 = operator.derive(diffFunc1);

            FunctionsIO.serialize(bufferedOutputStream, function);
            FunctionsIO.serialize(bufferedOutputStream, diffFunc1);
            FunctionsIO.serialize(bufferedOutputStream, diffFunc2);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(
                new FileInputStream("output/serialized linked list functions.bin"))) {

            TabulatedFunction deserializedFunc1 = FunctionsIO.deserialize(bufferedInputStream);
            TabulatedFunction deserializedFunc2 = FunctionsIO.deserialize(bufferedInputStream);
            TabulatedFunction deserializedFunc3 = FunctionsIO.deserialize(bufferedInputStream);

            System.out.println(deserializedFunc1.toString());
            System.out.println(deserializedFunc2.toString());
            System.out.println(deserializedFunc3.toString());

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}