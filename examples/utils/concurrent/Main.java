package utils.concurrent;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.lang.management.ThreadInfo;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * User: Bar Orion Barak
 * Date: Feb 20, 2012
 * Time: 7:55:42 PM
 */
public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        new DeadlockDetector(1, TimeUnit.SECONDS,
                new DeadlockListener() {
                    @Override
                    public void onDeadlock(List<ThreadInfo> deadLockThreads) {
                        logger.fatal("Found deadlock:" + deadLockThreads);
                        System.exit(-1);
                    }
                });
        Deadlock.create(Executors.newCachedThreadPool());
        Thread.sleep(Integer.MAX_VALUE);
    }
}
