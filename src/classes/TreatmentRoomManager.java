package classes;

/**
 * Manages access to the two hospital treatment rooms using a counting semaphore.
 *
 * OWNER: Mohamed / Omar
 */
public class TreatmentRoomManager {

    private static final int TOTAL_ROOMS = 2;
    private final Semaphore roomSemaphore;

    public TreatmentRoomManager() {
        this.roomSemaphore = new Semaphore(TOTAL_ROOMS);
    }

    public boolean tryAcquireRoom(long timeoutMs) {
        return roomSemaphore.tryAcquire(timeoutMs);
    }

    public void releaseRoom() {
        roomSemaphore.release();
    }
}
