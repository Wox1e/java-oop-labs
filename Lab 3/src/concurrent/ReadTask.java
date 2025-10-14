package concurrent;

import functions.TabulatedFunction;

public class ReadTask implements Runnable{
    private TabulatedFunction function;
    public ReadTask(TabulatedFunction func) {
        function = func;
    }

    @Override
    public void run() {
        for (int i = 0;i<function.getCount();i++)
            System.out.printf("After read: i = %d, x = %f, y = %f\n", i, function.getX(i), function.getY(i));
    }
}
