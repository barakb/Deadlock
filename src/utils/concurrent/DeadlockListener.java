package utils.concurrent;

import java.lang.management.ThreadInfo;
import java.util.List;

/**
 * User: Barak Bar Orion
 * Date: Feb 20, 2012
 * Time: 8:42:30 AM
 */
public interface DeadlockListener {
    void onDeadlock(List<ThreadInfo> deadLockThreads);
}