Deadlock -- A runtime deadlocks detector.
========================================
## DESCRIPTION
This small utility use JMX (threadsMXbean.findMonitorDeadlockedThreads()) to find deadlocks while program is running.
Sample Usage:

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

See _examples_ directory.