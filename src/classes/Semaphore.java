package classes;

import java.security.InvalidParameterException;

/**
 * Counting semaphore (as described in the labs).
 *
 * OWNER: Mamdouh
 *
 * CONTRACT other classes rely on:
 *  - acquire() / release() must NOT declare checked exceptions.
 *    If the thread is interrupted, restore the flag and throw a RuntimeException.
 *  - tryAcquire(0) must return immediately (true if a permit was free, else false).
 *  - tryAcquire(timeoutMs) must return false only after ~timeoutMs have really passed.
 *  - Use monitors (synchronized / wait / notify), NOT java.util.concurrent.Semaphore.
 */
public class Semaphore {

    private int permits;

    public Semaphore(int initialPermits) {
        if (initialPermits < 0) {
            throw new InvalidParameterException("Initial permits can't be negative");
        }
        this.permits = initialPermits;
    }

    /** Blocks until a permit is available, then takes it. */
    public synchronized void acquire() {
        while (permits == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted while acquiring permit", e);
            }
        }
        permits--;
    }

    /**
     * Tries to take a permit, waiting at most timeoutMs.
     *
     * @return true if a permit was taken, false if the time ran out
     */
    public synchronized boolean tryAcquire(long timeoutMs) {
        if (timeoutMs <= 0) {
            if (permits == 0) {
                return false;
            }
            permits--;
            return true;
        }

        long deadline = System.currentTimeMillis() + timeoutMs;

        while (permits == 0) {
            long remaining = deadline - System.currentTimeMillis();

            if (remaining <= 0) {
                return false;
            }

            try {
                wait(remaining);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted while trying to acquire permit", e);
            }
        }

        permits--;
        return true;
    }

    /** Returns a permit and wakes one waiting thread. */
    public synchronized void release() {
        permits++;
        notify();
    }

    public synchronized int getAvailablePermits() {
        return permits;
    }
}
