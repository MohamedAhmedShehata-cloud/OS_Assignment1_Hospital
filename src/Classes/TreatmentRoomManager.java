package Classes;
/**
 * Guards the hospital's treatment rooms (a resource independent from the waiting room).
 *
 * OWNER: (you)
 *
 * CONTRACT other classes rely on:
 *  - There are exactly TOTAL_ROOMS (2) rooms in the whole hospital.
 *  - tryAcquireRoom(ms) returns true if a room was taken, false if none became
 *    free within ms milliseconds.
 *  - releaseRoom() gives a room back.
 */
public class TreatmentRoomManager {

    private static final int TOTAL_ROOMS = 2;

    private final Semaphore roomSemaphore;

    public TreatmentRoomManager() {
        // TODO: initialize roomSemaphore
        this.roomSemaphore = null;
    }

    public boolean tryAcquireRoom(long timeoutMs) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    public void releaseRoom() {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }
}