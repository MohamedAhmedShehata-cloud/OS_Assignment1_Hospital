package Classes;
/* Finished */
public class TreatmentRoomManager {

    private static final int TOTAL_ROOMS = 2;

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