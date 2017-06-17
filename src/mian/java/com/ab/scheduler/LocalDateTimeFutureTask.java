package com.ab.scheduler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.FutureTask;

/**
 * A {@link FutureTask} that is used by {@link LocalDateTimeThreadPoolExecutor}.
 * 
 * @author bondarev
 *
 * @param <V>
 */
class LocalDateTimeFutureTask<V> extends FutureTask<V> implements Comparable<LocalDateTimeFutureTask<?>> {

    private final LocalDateTime ldt;
    
    private final TimeService timeService;

    LocalDateTimeFutureTask(LocalDateTimeCallable<V> callable) {
        this(callable, TimeServiceDefault.instance);
    }

    LocalDateTimeFutureTask(LocalDateTimeCallable<V> callable, TimeService timeService) {
        super(callable);
        this.ldt = callable.when();
        this.timeService = timeService;
    }

    LocalDateTime when() {
        return ldt;
    }

    long getDelay() {
        ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
        return Duration.between(timeService.now(), zdt.toInstant()).toNanos();
    }

    @Override
    public int compareTo(LocalDateTimeFutureTask<?> o) {
        return ldt.compareTo(o.when());
    }
}
