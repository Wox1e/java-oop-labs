package concurrent;

import functions.TabulatedFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReadTask implements Runnable{
    private static final Logger logger = LogManager.getLogger(ReadTask.class);
    private final TabulatedFunction function;
    private final Object object;
    public ReadTask(TabulatedFunction func, Object object) {
        this.object = object;
        function = func;
    }

    @Override
    public void run() {
        for (int i = 0; i < function.getCount(); i++) {
            synchronized (object) {
                logger.info("After read: i = {}, x = {}, y = {}\n", i, function.getX(i), function.getY(i));
            }
        }
    }
}