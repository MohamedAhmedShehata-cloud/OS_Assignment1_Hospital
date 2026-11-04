package Classes;

/*
Owner: (Mohamed , Omar)
*/
public class TreatmentRoomManager {
    // static total rooms to change it in one place
    private static final int TOTAL_ROOMS = 2;
    // composition
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