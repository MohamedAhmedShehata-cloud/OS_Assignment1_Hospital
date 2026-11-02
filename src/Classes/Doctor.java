package Classes;

import java.util.concurrent.ThreadLocalRandom;

public class Doctor implements Runnable {

    private static final long ROOM_ACQUIRE_TIMEOUT_MS = 3000;
    private static final int MIN_TREATMENT_MS = 2000;
    private static final int MAX_TREATMENT_MS = 5000;

    private final int id;
    private volatile DoctorState state = DoctorState.IDLE;
    private Patient currentPatient;

    private final WaitingRoom waitingRoom;
    private final TreatmentRoomManager treatmentRooms;
    private final Hospital hospital;

    public Doctor(int id, WaitingRoom waitingRoom,
                  TreatmentRoomManager treatmentRooms, Hospital hospital) {
        this.id = id;
        this.waitingRoom = waitingRoom;
        this.treatmentRooms = treatmentRooms;
        this.hospital = hospital;
    }

    @Override
    public void run() {
        try {
            while (hospital.isSimulationActive()) {
                state = DoctorState.IDLE;
                Patient patient = waitingRoom.takeNextPatient(this);
                if (patient == null) {
                    break; // simulation finished while we were idle
                }
                handlePatient(patient);
            }
        } catch (RuntimeException e) {
            // An interrupted semaphore wait ends this doctor cleanly.
            Thread.currentThread().interrupt();
        }
    }

    private void handlePatient(Patient patient) {
        currentPatient = patient;
        state = DoctorState.ASSIGNED;
        patient.setState(PatientState.ASSIGNED_TO_DOCTOR);
        hospital.logEvent(getName() + " is assigned to " + patient.getName() + ".");

        state = DoctorState.ACQUIRING_ROOM;
        hospital.logEvent(getName() + " is trying to acquire a treatment room.");

        if (treatmentRooms.tryAcquireRoom(ROOM_ACQUIRE_TIMEOUT_MS)) {
            hospital.logEvent(getName() + " acquired a treatment room.");
            treatPatient(patient);
            becomeAvailable();
            patient.notifyTreatmentFinished(); // patient then logs "left"
        } else {
            hospital.logEvent(getName() + " could not acquire a treatment room.");
            returnPatientToWaitingRoom(patient);
            becomeAvailable();
        }
    }

    /** Runs the treatment and always gives the room back, even on interrupt. */
    private void treatPatient(Patient patient) {
        state = DoctorState.TREATING;
        patient.setState(PatientState.IN_TREATMENT);
        hospital.logEvent(getName() + " begins treating " + patient.getName() + ".");
        try {
            Thread.sleep(ThreadLocalRandom.current()
                    .nextInt(MIN_TREATMENT_MS, MAX_TREATMENT_MS + 1));
            patient.setState(PatientState.TREATED);
            hospital.logEvent(getName() + " finished treating " + patient.getName() + ".");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // Log BEFORE releasing: once the permit is back another doctor may
            // acquire it immediately, and its log line must not appear first.
            hospital.logEvent(getName() + " released the treatment room.");
            treatmentRooms.releaseRoom();
        }
    }

    private void returnPatientToWaitingRoom(Patient patient) {
        hospital.logEvent(getName() + " is sending " + patient.getName()
                + " back to the waiting room.");
        waitingRoom.enterWaitingRoom(patient); // may block while the room is full
    }

    private void becomeAvailable() {
        currentPatient = null;
        state = DoctorState.IDLE;
        hospital.logEvent(getName() + " is now available.");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return "Doctor " + id;
    }

    public DoctorState getState() {
        return state;
    }
}
