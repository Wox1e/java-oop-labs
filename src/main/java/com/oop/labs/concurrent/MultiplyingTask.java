package com.oop.labs.concurrent;

import com.oop.labs.functions.TabulatedFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MultiplyingTask implements Runnable{

    private static final Logger logger = LogManager.getLogger(MultiplyingTask.class);
    private final TabulatedFunction function;

    public MultiplyingTask(TabulatedFunction function) {
        this.function = function;
    }

    @Override
    public void run() {
        logger.info("Поток MultiplyingTask запущен");
        for (int i = 0; i < function.getCount(); i++) {
            synchronized (function) {
                double currentY = function.getY(i);
                function.setY(i, currentY * 2);
            }
        }
        logger.info("Поток MultiplyingTask завершил работу");
    }
}
