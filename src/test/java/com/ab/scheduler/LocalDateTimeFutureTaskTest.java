package com.ab.scheduler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class LocalDateTimeFutureTaskTest {

    LocalDateTime now = LocalDateTime.of(2017, Month.JUNE, 17, 0, 0, 0, 0);

    @Mock
    TimeService timeService;
    
    int invocationCounter;

    @Test
    public void test() throws InterruptedException, ExecutionException {
        when(timeService.now()).thenReturn(toInstant(now));

        LocalDateTimeFutureTask<Integer> f1000 = newTask(1000, () -> 1);
        LocalDateTimeFutureTask<Integer> f1000_ = newTask(1000, () -> 2);
        LocalDateTimeFutureTask<Integer> f2000 = newTask(2000, () -> 3);

        assertTrue(f1000.compareTo(f2000) < 0);
        assertTrue(f1000.compareTo(f1000_) == 0);
        assertTrue(f2000.compareTo(f1000) > 0);

        assertEquals(1000, f1000.getDelay());
        assertEquals(2000, f2000.getDelay());

        LocalDateTimeQueue queue = new LocalDateTimeQueue();
        queue.add(f2000);
        queue.add(f1000);
        queue.add(f1000_);
        
        // 1
        invocationCounter = 0;
        when(timeService.now()).then((ic) -> {
            invocationCounter++;
            if (invocationCounter == 1) {
                return toInstant(now);
            }
            return toInstant(now.plusNanos(1000));
        });
        @SuppressWarnings("unchecked")
        LocalDateTimeFutureTask<Integer> task1 = (LocalDateTimeFutureTask<Integer>) queue.take();
        task1.run();
        assertEquals(Integer.valueOf(1), task1.get());
        
        // 2
        invocationCounter = 0;
        when(timeService.now()).then((ic) -> {
            return toInstant(now.plusNanos(2010));
        });
        @SuppressWarnings("unchecked")
        LocalDateTimeFutureTask<Integer> task2 = (LocalDateTimeFutureTask<Integer>) queue.take();
        task2.run();
        assertEquals(Integer.valueOf(2), task2.get());
        
        // 3
        invocationCounter = 0;
        when(timeService.now()).then((ic) -> {
            return toInstant(now.plusNanos(2010));
        });
        @SuppressWarnings("unchecked")
        LocalDateTimeFutureTask<Integer> task3 = (LocalDateTimeFutureTask<Integer>) queue.take();
        task3.run();
        assertEquals(Integer.valueOf(3), task3.get());
    }

    private Instant toInstant(LocalDateTime ldt) {
        return ldt.atZone(ZoneId.systemDefault()).toInstant();
    }

    private <V> LocalDateTimeFutureTask<V> newTask(long nanos, Callable<V> callable) {
        LocalDateTime ldt = now.plusNanos(nanos);
        LocalDateTimeCallable<V> ldtCallable = new LocalDateTimeCallable<>(ldt, callable);
        return new LocalDateTimeFutureTask<>(ldtCallable, timeService);
    }
}
