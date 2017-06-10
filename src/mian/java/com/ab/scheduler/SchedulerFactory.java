package com.ab.scheduler;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;

/**
 * Factory class for creating schedulers.
 * 
 * @author bondarev
 */
public abstract class SchedulerFactory {

    private static class CurrentSchedulerSingletonHolder {
        static Scheduler current;
        static {
            current = new Scheduler() {
                Scheduler target = createDefault();
                @Override
                public <V> ScheduledFuture<V> schedule(LocalDateTime when, Callable<V> callable) {
                    return target.schedule(when, callable);
                }

                @Override
                public void shutdown() {
                    // current scheduler ignores shutdown because it is a singleton.
                }

                @Override
                public void shutdownNow() {
                    // current scheduler ignores shutdown because it is a singleton.
                }
            };
        }
    }

    /**
     * @return Single thread scheduler.
     */
    public static Scheduler createSingle() {
        return new SchedulerImpl(1);
    }

    /**
     * @return Scheduler with number of threads equals to number of available processors.
     */
    public static Scheduler createDefault() {
        return new SchedulerImpl(Runtime.getRuntime().availableProcessors());
    }

    /**
     * @return Shared current scheduler.
     */
    public static Scheduler current() {
        return CurrentSchedulerSingletonHolder.current;
    }
}
