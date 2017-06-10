package com.ab.scheduler;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Scheduler implementation.
 * 
 * @author bondarev
 */
class SchedulerImpl implements Scheduler {

    private final ScheduledExecutorService executor;

    SchedulerImpl() {
        this(1);
    }

    SchedulerImpl(int poolSize) {
        this(Executors.newScheduledThreadPool(poolSize, new ThreadFactory() {
            AtomicInteger sequencer = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "schedule-executor-" + sequencer.getAndIncrement());
            }
        }));
    }

    SchedulerImpl(ScheduledExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public <V> ScheduledFuture<V> schedule(LocalDateTime when, Callable<V> callable) {
        ZonedDateTime zdt = when.atZone(ZoneId.systemDefault());
        Instant instant = zdt.toInstant();
        long delay = Duration.between(Instant.now(), instant).toNanos();
        return executor.schedule(callable, delay, TimeUnit.NANOSECONDS);
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
