package com.oop.labs.concurrent;

import com.oop.labs.functions.ConstantFunction;
import com.oop.labs.functions.LinkedListTabulatedFunction;
import com.oop.labs.functions.TabulatedFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReadWriteTaskExecutor {
    private static final Logger logger = LogManager.getLogger(ReadWriteTaskExecutor.class);

    public static void main(String[] args) throws InterruptedException {
        Object object = new Object();
        ConstantFunction constantFunction = new ConstantFunction(-1);
        TabulatedFunction function = new LinkedListTabulatedFunction(constantFunction, 1, 1000, 1000);

        ReadTask readTask = new ReadTask(function, object);
        WriteTask writeTask = new WriteTask(function, 0.5, object);

        Thread readThread = new Thread(readTask);
        Thread writeThread = new Thread(writeTask);

        logger.info("Запуск writeThread");
        writeThread.start();
        Thread.sleep(50);
        logger.info("Запуск readThread");
        readThread.start();
    }
}