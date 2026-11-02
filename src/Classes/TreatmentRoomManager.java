package Classes;

/**
 * Guards the hospital's treatment rooms.
 *
 * <p>Independent from the waiting room: it owns its own counting semaphore
 * whose permits are the rooms that are currently free.
 */
public class TreatmentRoomManager {

    private static final int TOTAL_ROOMS = 2;

    private final Semaphore roomSemaphore = new Semaphore(TOTAL_ROOMS);

    /**
     * Tries to take a free room.
     *
     * @param timeoutMs maximum time to wait for a room
     * @return true if a room was acquired, false if the time ran out
     */
    public boolean tryAcquireRoom(long timeoutMs) {
        return roomSemaphore.tryAcquire(timeoutMs);
    }

    /** Gives a previously acquired room back. */
    public void releaseRoom() {
        roomSemaphore.release();
    }
}
