package com.ab.scheduler;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Scheduler implementation.
 * 
 * @author bondarev
 */
class SchedulerImpl implements Scheduler {

    private final LocalDateTimeThreadPoolExecutor executor;

    SchedulerImpl() {
        this(1);
    }

    SchedulerImpl(int poolSize) {
        executor = new LocalDateTimeThreadPoolExecutor(poolSize);
    }

    @Override
    public <V> Future<V> schedule(LocalDateTime when, Callable<V> callable) {
        LocalDateTimeCallable<V> task = new LocalDateTimeCallable<>(when, callable);
        return executor.submit(task);
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }

    @Override
    public void shutdownNow() {
        executor.shutdownNow();
    }

}
