package com.ab.scheduler;

import static java.time.LocalDateTime.*;
import static java.time.temporal.ChronoUnit.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SchedulerTest {

    private static final int NUM_TESTS = 10;

    private static final int NUM_TASKS = 3;
    
    private static final int STEP_MILLIS = 10;

    private static final String EXCEPTION_MSG = "message1";

    private Scheduler schedulerSingle;

    private static interface Validator {
        void validate(List<Integer> nums);
    }

    @Before
    public void setUp() {
        schedulerSingle = SchedulerFactory.createSingle();
    }

    @After
    public void tearDown() {
        schedulerSingle.shutdown();
    }

    @Test(timeout = STEP_MILLIS * NUM_TASKS * NUM_TASKS + 1000)
    public void testSchedulerSingle() {
        // results must be ordered in the case of single scheduler
        Validator validator = (results) -> {
            assertEquals(NUM_TASKS, results.size());
            for (int t = 0; t < NUM_TASKS; t++) {
                assertEquals(Integer.valueOf(t), results.get(t));
            }
        };
        test(schedulerSingle, validator);
    }

    @Test(timeout = STEP_MILLIS * NUM_TASKS * NUM_TASKS + 1000)
    public void testSchedulerMultiple() {
        // results might be in any order
        Validator validator = (results) -> {
            assertEquals(NUM_TASKS, results.size());
            for (int t = 0; t < NUM_TASKS; t++) {
                assertTrue(results.contains(t));
            }
        };
        test(SchedulerFactory.current(), validator);
    }

    private void test(Scheduler scheduler, Validator validator) {
        for (int i = 0; i < NUM_TESTS; i++) {
            List<Integer> results = Collections.synchronizedList(new ArrayList<>());

            List<Future<?>> futures = new ArrayList<>(NUM_TASKS);
            for (int t = 0; t < NUM_TASKS; t++) {
                int runNum = t;
                ScheduledFuture<Integer> f = scheduler.schedule(now().plus(STEP_MILLIS * (runNum + 1), MILLIS),
                        () -> {
                            ThreadLocalRandom rnd = ThreadLocalRandom.current();
                            Thread.sleep(STEP_MILLIS * rnd.nextInt(NUM_TASKS));
                            results.add(runNum);
                            return runNum;
                        });
                futures.add(f);
            }

            waitAll(futures);

            validator.validate(results);
        }
    }

    private void waitAll(Iterable<Future<?>> futures) {
        for (Future<?> f : futures) {
          try {
            f.get();
          } catch(Exception e) {
            fail(e.getMessage());
          }
        }
     }

    @Test(timeout = 2 * STEP_MILLIS + 1000)
    public void testScheduleExceptional() throws InterruptedException, ExecutionException {
        LocalDateTime now = LocalDateTime.now();
        ScheduledFuture<Integer> future = schedulerSingle.schedule(now.plus(STEP_MILLIS, MILLIS),
                () -> {
                    Thread.sleep(STEP_MILLIS);
                    throw new IllegalArgumentException(EXCEPTION_MSG);
                });
        try {
            future.get();
            fail("ExecutionException expected.");
        } catch (ExecutionException e) {
            assertEquals(EXCEPTION_MSG, e.getCause().getMessage());
        }
    }

}
