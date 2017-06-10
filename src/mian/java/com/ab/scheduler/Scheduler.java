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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Scheduler {

    private final ScheduledExecutorService executor;

    private final AtomicInteger sequencer = new AtomicInteger();

    private Scheduler(int poolSize) {
        executor = Executors.newScheduledThreadPool(poolSize,
                (Runnable r) -> new Thread(r, "schedule-executor-" + sequencer.getAndIncrement()));
    }

    public static Scheduler create() {
        return create(Runtime.getRuntime().availableProcessors());
    }

    public static Scheduler create(int poolSize) {
        return new Scheduler(poolSize);
    }

    public <V> ScheduledFuture<V> submit(LocalDateTime when, Callable<V> callable) {
        ZonedDateTime zdt = when.atZone(ZoneId.systemDefault());
        Instant instant = zdt.toInstant();
        long delay = Duration.between(Instant.now(), instant).toNanos();
        return executor.schedule(callable, delay, TimeUnit.NANOSECONDS);
    }
}
