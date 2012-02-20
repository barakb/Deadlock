package utils.concurrent;

import org.apache.log4j.Logger;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;

/**
 * User: Bar Orion Barak
 * Date: Feb 20, 2012
 * Time: 7:13:29 PM
 */
public class Deadlock implements Runnable {
    private static final Logger logger = Logger.getLogger(Deadlock.class);
    private Object[] locks;
    private CyclicBarrier barrier;

    public Deadlock(CyclicBarrier barrier, Object... locks) {
        this.barrier = barrier;
        this.locks = locks;
    }

    @Override
    public void run() {
        try {
            synchronized (locks[0]) {
                barrier.await();
                synchronized (locks[1]) {
                    synchronized (Deadlock.class) {
                        Deadlock.class.wait();
                    }
                }
            }
        } catch (InterruptedException ignored) {
        } catch (BrokenBarrierException ignored) {
        }
    }

    public static void create(Executor executor) {
        Object lock1 = new Object();
        Object lock2 = new Object();
        CyclicBarrier barrier = new CyclicBarrier(2);
        executor.execute(new Deadlock(barrier, lock1, lock2));
        executor.execute(new Deadlock(barrier, lock2, lock1));
    }
}
