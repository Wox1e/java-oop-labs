package concurrent;

import functions.ArrayTabulatedFunction;
import functions.ConstantFunction;
import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;

public class ReadWriteTaskExecutor {
    public static void main(String[] args) throws InterruptedException {
        Object object = new Object();
        ConstantFunction constantFunction = new ConstantFunction(-1);
        TabulatedFunction function = new LinkedListTabulatedFunction(constantFunction, 1, 1000, 1000);
        TabulatedFunction func = new ArrayTabulatedFunction(constantFunction, 1, 1000, 1000);

        ReadTask readTask = new ReadTask(function, object);
        WriteTask writeTask = new WriteTask(function, 0.5, object);

        Thread readThread = new Thread(readTask);
        Thread writeThread = new Thread(writeTask);

        writeThread.start();
        Thread.sleep(50);
        readThread.start();
    }
}