package com.oop.labs.io;

import com.oop.labs.functions.TabulatedFunction;
import com.oop.labs.functions.factory.ArrayTabulatedFunctionFactory;
import com.oop.labs.functions.factory.LinkedListTabulatedFunctionFactory;
import com.oop.labs.functions.factory.TabulatedFunctionFactory;
import com.oop.labs.operations.TabulatedDifferentialOperator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class TabulatedFunctionFileInputStream {
    private static final Logger logger = LogManager.getLogger(TabulatedFunctionFileInputStream.class);
    public static void main(String[] args) {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("input/binary function.bin"))) {
            TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
            TabulatedFunction function = FunctionsIO.readTabulatedFunction(bufferedInputStream, factory);
            logger.info(function.toString());
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
            logger.info("Производная функции:");
            logger.info(derivative.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
