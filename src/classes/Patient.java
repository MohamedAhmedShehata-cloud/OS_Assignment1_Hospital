package classes;

/**
 * A patient thread.
 *
 * OWNER: Youssef
 *
 * run() must do:
 *   1. log "<name> arrived."
 *   2. waitingRoom.enterWaitingRoom(this)   (WaitingRoom logs entering / waiting outside)
 *   3. treatmentDoneSemaphore.acquire()     (sleep until the doctor finishes)
 *   4. setState(LEFT), log "<name> left."
 *   5. hospital.onPatientLeft(this)
 */
public class Patient implements Runnable {

    private final int id;
    private volatile PatientState state = PatientState.ARRIVED;
    private final WaitingRoom waitingRoom;
    private final Hospital hospital;
    private final Semaphore treatmentDoneSemaphore = new Semaphore(0);

    public Patient(int id, WaitingRoom waitingRoom, Hospital hospital) {
        this.id = id;
        this.waitingRoom = waitingRoom;
        this.hospital = hospital;
    }

    @Override
    public void run() {
        // 1. log "<name> arrived."
        hospital.logEvent(getName() + " arrived.");

        // 2. waitingRoom.enterWaitingRoom(this)
        waitingRoom.enterWaitingRoom(this);

        // 3. treatmentDoneSemaphore.acquire() (sleep until the doctor finishes)
        treatmentDoneSemaphore.acquire();

        // 4. setState(LEFT), log "<name> left."
        setState(PatientState.LEFT);
        hospital.logEvent(getName() + " left.");

        // 5. hospital.onPatientLeft(this)
        hospital.onPatientLeft(this);
    }

    /** Called by the doctor when treatment is over; wakes the patient. */
    public void notifyTreatmentFinished() {
        treatmentDoneSemaphore.release();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return "P" + id;
    }

    public PatientState getState() {
        return state;
    }

    public void setState(PatientState state) {
        this.state = state;
    }
}