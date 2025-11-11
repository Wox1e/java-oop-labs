package com.oop.labs.io;

import com.oop.labs.functions.*;
import com.oop.labs.functions.factory.ArrayTabulatedFunctionFactory;
import com.oop.labs.functions.factory.LinkedListTabulatedFunctionFactory;
import com.oop.labs.functions.factory.TabulatedFunctionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;


public class TabulatedFunctionFileReader {
    private static final Logger logger = LogManager.getLogger(TabulatedFunctionFileReader.class);
    public static void main(String[] args){
        try (FileReader arrayReader = new FileReader("input/function.txt");
             BufferedReader arrayBufferedReader = new BufferedReader(arrayReader);
             FileReader listReader = new FileReader("input/function.txt");
             BufferedReader listBufferedReader = new BufferedReader(listReader)
        ){

            TabulatedFunctionFactory arrayFactory = new ArrayTabulatedFunctionFactory();
            TabulatedFunctionFactory listFactory = new LinkedListTabulatedFunctionFactory();

            TabulatedFunction arrayFunc = FunctionsIO.readTabulatedFunction(arrayBufferedReader, arrayFactory);
            TabulatedFunction listFunc = FunctionsIO.readTabulatedFunction(listBufferedReader, listFactory);

            logger.info("Array Func -> " + arrayFunc);
            logger.info("List Func -> " + listFunc);

       } catch (FileNotFoundException e) {
           throw new RuntimeException("Файл не найден");
       } catch (IOException e) {
            e.printStackTrace();
       }
    }
}
