package com.oop.labs.concurrent;

import com.oop.labs.functions.TabulatedFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WriteTask implements Runnable {
    private final TabulatedFunction function;
    private static final Logger logger = LogManager.getLogger(WriteTask.class);
    private final double value;
    private final Object object;

    public WriteTask(TabulatedFunction func, double val, Object object) {
        this.object = object;
        function = func;
        value = val;
    }

    @Override
    public void run() {
        for (int i = 0; i < function.getCount(); i++) {
            synchronized (object) {
                function.setY(i, value);
                logger.info("Writing for index {} complete\n", i);
            }
        }
    }
}