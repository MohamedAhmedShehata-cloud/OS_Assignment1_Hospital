package Classes;

/**
 * Counting semaphore (as described in the labs).
 *
 * OWNER: Mamdouh
 *
 * CONTRACT other classes rely on - please keep it exactly:
 *  - acquire() / release() must NOT declare checked exceptions.
 *    If the thread is interrupted, restore the flag and throw a RuntimeException.
 *  - tryAcquire(0) must return immediately (true if a permit was free, else false).
 *  - tryAcquire(timeoutMs) must return false only after ~timeoutMs have really passed.
 *  - Use monitors (synchronized / wait / notify), NOT java.util.concurrent.Semaphore.
 */
public class Semaphore {

    private int permits;

    public Semaphore(int initialPermits) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    /** Blocks until a permit is available, then takes it. */
    public void acquire() {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Tries to take a permit, waiting at most timeoutMs.
     *
     * @return true if a permit was taken, false if the time ran out
     */
    public boolean tryAcquire(long timeoutMs) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    /** Returns a permit and wakes one waiting thread. */
    public void release() {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    public int getAvailablePermits() {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }
}
