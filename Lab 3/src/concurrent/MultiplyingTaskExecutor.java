package concurrent;

import functions.LinkedListTabulatedFunction;
import functions.UnitFunction;

import java.util.ArrayList;
import java.util.List;

public class MultiplyingTaskExecutor {
    public static void main(String[] args) throws InterruptedException {
        UnitFunction unitFunction = new UnitFunction();
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(unitFunction, 1, 1000, 500);
        List<Thread> threadList = new ArrayList<Thread>();
        for (int i = 0; i < 10; i++) {
            MultiplyingTask task = new MultiplyingTask(func);
            Thread taskThread = new Thread(task);
            threadList.add(taskThread);
        }

        for (Thread thread:threadList){
            thread.start();
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < func.getCount(); i++) {
            double x = func.getX(i);
            double y = func.getY(i);
            System.out.println("Элемент " + i + ": " + x + ", " + y)    ;
        }


    }
}
