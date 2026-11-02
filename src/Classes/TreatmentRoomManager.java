package Classes;

/* Finished */
public class TreatmentRoomManager {
    // static total rooms to change it in one place
    private static final int TOTAL_ROOMS = 2;
    // composition
    private final Semaphore roomSemaphore;

    public TreatmentRoomManager() {
        // Done BY Mohamed Ahmed
        this.roomSemaphore = new Semaphore(TOTAL_ROOMS);
    }

    public boolean tryAcquireRoom(long timeoutMs) {
        // Done by Mohamed Ahmed
        return roomSemaphore.tryAcquire(timeoutMs);
    }

    public void releaseRoom() {
        // Done By Mohamed Ahmed
        roomSemaphore.release();
    }
}