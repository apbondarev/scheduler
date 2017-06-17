package com.ab.scheduler;

import static java.util.concurrent.TimeUnit.*;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadPoolExecutor that start a task execution according to LocalDateTime.
 * It orders tasks with priority queue by {@link LocalDateTime}.
 * 
 * @author bondarev
 *
 */
class LocalDateTimeThreadPoolExecutor extends ThreadPoolExecutor {

    LocalDateTimeThreadPoolExecutor(int poolSize) {
        super(poolSize, Integer.MAX_VALUE, 0, NANOSECONDS, new LocalDateTimeQueue(), new ThreadFactory() {
            AtomicInteger sequencer = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "schedule-executor-" + sequencer.getAndIncrement());
            }
        });
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new LocalDateTimeFutureTask<T>((LocalDateTimeCallable<T>) callable);
    }

}
