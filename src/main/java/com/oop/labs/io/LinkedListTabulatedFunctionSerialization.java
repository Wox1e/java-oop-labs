package com.oop.labs.io;

import com.oop.labs.functions.LinkedListTabulatedFunction;
import com.oop.labs.functions.TabulatedFunction;
import com.oop.labs.operations.TabulatedDifferentialOperator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class LinkedListTabulatedFunctionSerialization {
    private static final Logger logger = LogManager.getLogger(LinkedListTabulatedFunctionSerialization.class);
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

            logger.info(deserializedFunc1.toString());
            logger.info(deserializedFunc2.toString());
            logger.info(deserializedFunc3.toString());

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}