package io;

import functions.ArrayTabulatedFunction;
import functions.TabulatedFunction;
import operations.TabulatedDifferentialOperator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class ArrayTabulatedFunctionSerialization {
    private static final Logger logger = LogManager.getLogger(ArrayTabulatedFunctionSerialization.class);
    public static void main(String [] args){
        try (FileOutputStream fileOutputStream = new FileOutputStream("output/serialized_array_functions.bin");
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
             ){

            double[] xVals = {1,4,8,12};
            double[] yVals = {52, 228, 1337, 666};

            TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();
            ArrayTabulatedFunction func = new ArrayTabulatedFunction(xVals, yVals);
            TabulatedFunction diffFunc = operator.derive(func);
            TabulatedFunction diffFunc2 = operator.derive(diffFunc);

            FunctionsIO.serialize(bufferedOutputStream, func);
            FunctionsIO.serialize(bufferedOutputStream, diffFunc);
            FunctionsIO.serialize(bufferedOutputStream, diffFunc2);

        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("File not found");
        } catch (IOException e) {
            e.printStackTrace();
        }


        try (FileInputStream fileInputStream = new FileInputStream("output/serialized_array_functions.bin");
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {

            TabulatedFunction func = FunctionsIO.deserialize(bufferedInputStream);
            TabulatedFunction diffFunc = FunctionsIO.deserialize(bufferedInputStream);
            TabulatedFunction diffFunc2 = FunctionsIO.deserialize(bufferedInputStream);

            logger.info("Func -> " + func);
            logger.info("diffFunc -> " + diffFunc);
            logger.info("diffFunc2 -> " + diffFunc2);

        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
