package com.oop.labs.concurrent;

import com.oop.labs.functions.LinkedListTabulatedFunction;
import com.oop.labs.functions.UnitFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class MultiplyingTaskExecutor {

    private static final Logger logger = LogManager.getLogger(MultiplyingTaskExecutor.class);


    public static void main(String[] args) throws InterruptedException {
        UnitFunction unitFunction = new UnitFunction();
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(unitFunction, 1, 1000, 500);
        List<Thread> threadList = new ArrayList<Thread>();
        for (int i = 0; i < 10; i++) {
            MultiplyingTask task = new MultiplyingTask(func);
            Thread taskThread = new Thread(task);
            threadList.add(taskThread);
        }

        Deque<Thread> startedThreads = new ArrayDeque<Thread>();

        for (Thread thread:threadList){
            thread.start();
            startedThreads.add(thread);
        }



        while (!startedThreads.isEmpty()){
            if (!startedThreads.getFirst().isAlive()){
                startedThreads.removeFirst();
            }
        }


        for (int i = 0; i < func.getCount(); i++) {
            double x = func.getX(i);
            double y = func.getY(i);
            logger.info("Элемент " + i + ": " + x + ", " + y)    ;
        }


    }
}
