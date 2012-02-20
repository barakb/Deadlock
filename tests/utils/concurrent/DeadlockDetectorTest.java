package utils.concurrent;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.management.ThreadInfo;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * User: Bar Orion Barak
 * Date: Feb 20, 2012
 * Time: 7:11:30 PM
 */
public class DeadlockDetectorTest {
    private static final Logger logger = Logger.getLogger(DeadlockDetectorTest.class);

    @BeforeClass
    public static void beforeClass() {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();
    }

    @Test(timeout = 10000)
    public void testDeadlockDetector() throws Exception {
        final BlockingQueue<List<ThreadInfo>> eventsQueue = new LinkedBlockingQueue<List<ThreadInfo>>();
        new DeadlockDetector(1, TimeUnit.SECONDS, new DeadlockListener() {
            @Override
            public void onDeadlock(List<ThreadInfo> deadlockThreads) {
                eventsQueue.offer(deadlockThreads);
            }
        });
        Deadlock.create(Executors.newCachedThreadPool());
        Thread.sleep(3000);
        Assert.assertThat(1, org.hamcrest.CoreMatchers.is(eventsQueue.size()));
    }
}
