package com.ab.scheduler;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;

/**
 * {@link Callable} with {@link LocalDateTime}.
 * It is used by {@link LocalDateTimeThreadPoolExecutor}
 * to submit a task for execution at specified time.
 * 
 * @author bondarev
 *
 * @param <V>
 */
class LocalDateTimeCallable<V> implements Callable<V> {

    private final LocalDateTime ldt;
    private final Callable<V> callable;

    LocalDateTimeCallable(LocalDateTime when, Callable<V> callable) {
        this.ldt = when;
        this.callable = callable;
    }

    /**
     * @return When execute the task.
     */
    LocalDateTime when() {
        return ldt;
    }

    @Override
    public V call() throws Exception {
        return callable.call();
    }

}
