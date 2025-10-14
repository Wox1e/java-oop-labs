package concurrent;

import functions.ConstantFunction;
import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;

public class ReadWriteTaskExecutor {
    static void main(String[] args) {
        ConstantFunction constantFunction = new ConstantFunction(-1);
        TabulatedFunction function = new LinkedListTabulatedFunction(constantFunction, 1, 1000, 1000);

        ReadTask readTask = new ReadTask(function);
        WriteTask writeTask = new WriteTask(function, 0.5);

        Thread readThread = new Thread(readTask);
        Thread writeThread = new Thread(writeTask);

        readThread.start();
        writeThread.start();
    }
}
