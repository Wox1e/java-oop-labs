package io;

import functions.*;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;

import java.io.*;


public class TabulatedFunctionFileReader {
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

            System.out.println("Array Func -> " + arrayFunc);
            System.out.println("List Func -> " + listFunc);

       } catch (FileNotFoundException e) {
           throw new RuntimeException("Файл не найден");
       } catch (IOException e) {
            e.printStackTrace();
       }
    }
}
