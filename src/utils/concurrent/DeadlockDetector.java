package utils.concurrent;

import org.apache.log4j.Logger;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * User: Barak Bar Orion
 * Date: Feb 20, 2012
 * Time: 8:39:36 AM
 */
public class DeadlockDetector {
    public static final Logger logger = Logger.getLogger(DeadlockDetector.class);
    private Set<DeadlockListener> listeners;
    private ThreadMXBean threadsMXbean;
    private ScheduledFuture<?> future;
    private Set<Long> alreadyNotified;

    public DeadlockDetector(DeadlockListener... listeners) {
        this(1, TimeUnit.MINUTES, listeners);
    }

    public DeadlockDetector(long period, TimeUnit timeUnit, DeadlockListener... listeners) {
        this(Executors.newSingleThreadScheduledExecutor(), period, timeUnit, listeners);
    }

    public DeadlockDetector(ScheduledExecutorService scheduledExecutorService, long period, TimeUnit timeUnit, DeadlockListener... listeners) {
        this.listeners = new HashSet<DeadlockListener>();
        this.alreadyNotified = new HashSet<Long>();
        this.listeners.addAll(Arrays.asList(listeners));
        this.threadsMXbean = ManagementFactory.getThreadMXBean();
        this.future = scheduledExecutorService.scheduleAtFixedRate(new DeadlockMonitorTask(), period, period, timeUnit);
    }

    public synchronized void addDeadlockListener(DeadlockListener listener) {
        listeners.add(listener);
    }

    public synchronized boolean removeDedlockListener(DeadlockListener listener) {
        return listeners.remove(listener);
    }

    private synchronized void notifyDeadlock(List<ThreadInfo> deadLockTreads) {
        for (DeadlockListener listener : listeners) {
            try {
                listener.onDeadlock(deadLockTreads);
            } catch (Throwable e) {
                logger.error(e, e);
            }
        }
    }

    public void shutdown() {
        if (future != null) {
            future.cancel(true);
        }
    }

    private boolean someWasNotNotified(long[] ids) {
        for (long id : ids) {
            if (!alreadyNotified.contains(id)) {
                return true;
            }
        }
        return false;
    }

    private class DeadlockMonitorTask implements Runnable {
        @Override
        public void run() {
            long[] ids = threadsMXbean.findMonitorDeadlockedThreads();
            if (ids != null && 0 < ids.length && someWasNotNotified(ids)) {
                List<ThreadInfo> deadLockTreads = new ArrayList<ThreadInfo>(ids.length);
                for (Long id : ids) {
                    alreadyNotified.add(id);
                    ThreadInfo ti = threadsMXbean.getThreadInfo(id, 30);
                    deadLockTreads.add(ti);
                }
                notifyDeadlock(deadLockTreads);
            }
        }
    }
}
