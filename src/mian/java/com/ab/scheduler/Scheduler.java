package com.ab.scheduler;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledFuture;

import javax.security.auth.callback.Callback;

/**
 * Scheduler
 * 
 * @author bondarev
 */
public interface Scheduler {

    /**
     * Execute {@link Callback} at given time. Past time events will be processed immediately. 
     * @param when
     * @param callable
     * @return a ScheduledFuture that can be used to extract result or cancel.
     */
    <V> ScheduledFuture<V> schedule(LocalDateTime when, Callable<V> callable);

    /**
     * Shutdown scheduler gracefully.
     * See {@link ExecutorService#shutdown()}.
     */
    void shutdown();

    /**
     * Shutdown scheduler forcedly.
     * See {@link ExecutorService#shutdownNow()}.
     */
    void shutdownNow();
}
