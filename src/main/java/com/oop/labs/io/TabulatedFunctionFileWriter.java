package com.oop.labs.io;

import com.oop.labs.functions.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TabulatedFunctionFileWriter {
    public static void main(String[] args){
        try (FileWriter arrayFileWriter = new FileWriter("output/array function.txt");
            BufferedWriter arrayBufferedWriter = new BufferedWriter(arrayFileWriter);
            FileWriter listFileWriter = new FileWriter("output/linked_list_function.txt");
            BufferedWriter listBufferedWriter = new BufferedWriter(listFileWriter);
            ){


            double[] xVals = {1,4,5,9};
            double[] yVals = {0,15,3,8};

            TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xVals, yVals);
            TabulatedFunction listFunc = new LinkedListTabulatedFunction(xVals, yVals);
            FunctionsIO.writeTabulatedFunction(arrayBufferedWriter, arrayFunc);
            FunctionsIO.writeTabulatedFunction(listBufferedWriter, listFunc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
