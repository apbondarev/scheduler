package com.ab.scheduler;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A priority queue for {@link ThreadPoolExecutor} that orders
 * {@link LocalDateTimeFutureTask}'s by {@link LocalDateTime}.
 * This code is copied from {@code DelayedWorkQueue} 
 * from {@link java.util.concurrent.ScheduledThreadPoolExecutor} 
 * with few minor changes to adopt ordering by time.
 * Most methods are not implemented and throw 
 * {@code UnsupportedOperationException}. Only those methods
 * that are used in {@code ThreadPoolExecutor} are implemented.
 * 
 * @author bondarev
 */
class LocalDateTimeQueue implements BlockingQueue<Runnable> {
    
    private final ReentrantLock lock = new ReentrantLock();
    
    private final PriorityQueue<LocalDateTimeFutureTask<?>> queue = new PriorityQueue<>();

    /**
     * Thread designated to wait for the task at the head of the
     * queue.
     */
    private Thread leader = null;

    /**
     * Condition signalled when a newer task becomes available at the
     * head of the queue or a new thread may need to become leader.
     */
    private final Condition available = lock.newCondition();
    
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean offer(Runnable e) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            LocalDateTimeFutureTask<?> task = (LocalDateTimeFutureTask<?>) e;
            queue.add(task);
            if (queue.peek() == e) {
                leader = null;
                available.signal();
            }
        } finally {
            lock.unlock();
        }
        return true;
    }

    @Override
    public Runnable take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            for (;;) {
                LocalDateTimeFutureTask<?> first = queue.peek();
                if (first == null) {
                    available.await();
                } else {
                    long delayNs = first.getDelay();
                    if (delayNs <= 0) {
                        return queue.poll();
                    }
                    first = null; // don't retain ref while waiting
                    if (leader != null) {
                        available.await();
                    } else {
                        Thread thisThread = Thread.currentThread();
                        leader = thisThread;
                        try {
                            available.awaitNanos(delayNs);
                        } finally {
                            if (leader == thisThread)
                                leader = null;
                        }
                    }
                }
            }
        } finally {
            if (leader == null && queue.peek() != null)
                available.signal();
            lock.unlock();
        }
    }

    @Override
    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int drainTo(Collection<? super Runnable> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int drainTo(Collection<? super Runnable> c, int maxElements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Runnable> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(Runnable e) {
        return offer(e);
    }

    @Override
    public void put(Runnable e) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean offer(Runnable e, long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable poll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable poll(long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable element() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable peek() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Runnable> iterator() {
        throw new UnsupportedOperationException();
    }
}