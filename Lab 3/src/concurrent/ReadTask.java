package concurrent;

import functions.TabulatedFunction;

public class ReadTask implements Runnable{
    private final TabulatedFunction function;
    private final Object object;
    public ReadTask(TabulatedFunction func, Object object) {
        this.object = object;
        function = func;
    }

    @Override
    public void run() {
        synchronized (object) {
            for (int i = 0; i < function.getCount(); i++)
                System.out.printf("After read: i = %d, x = %f, y = %f\n", i, function.getX(i), function.getY(i));
        }
    }
}
