package concurrent;

import functions.TabulatedFunction;

public class WriteTask implements Runnable {
    private final TabulatedFunction function;
    private final double value;
    private final Object object;

    public WriteTask(TabulatedFunction func, double val, Object object) {
        this.object = object;
        function = func;
        value = val;
    }

    @Override
    public void run() {
        synchronized (object) {
            for (int i = 0; i < function.getCount(); i++) {
                function.setY(i, value);
                System.out.printf("Writing for index %d complete\n", i);
            }
        }
    }
}
