package classes;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A doctor thread.
 * OWNER: Omar / Mohamed
 */
public class Doctor implements Runnable {

    private static final long ROOM_ACQUIRE_TIMEOUT_MS = 3000;

    private final int id;
    private volatile DoctorState state = DoctorState.IDLE;
    private Patient currentPatient;

    private final WaitingRoom waitingRoom;
    private final TreatmentRoomManager treatmentRooms;
    private final Hospital hospital;

    public Doctor(int id, WaitingRoom waitingRoom,
                  TreatmentRoomManager treatmentRooms,
                  Hospital hospital) {

        this.id = id;
        this.waitingRoom = waitingRoom;
        this.treatmentRooms = treatmentRooms;
        this.hospital = hospital;
    }

    @Override
    public void run() {
        while (hospital.isSimulationActive()) {
            try {
                state = DoctorState.IDLE;

                Patient patient = waitingRoom.takeNextPatient(this);

                if (patient == null) {
                    break;
                }

                currentPatient = patient;
                handlePatient(patient);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        state = DoctorState.IDLE;
        currentPatient = null;
    }

    private void handlePatient(Patient patient) throws InterruptedException {
        state = DoctorState.ASSIGNED;
        patient.setState(PatientState.ASSIGNED_TO_DOCTOR);
        hospital.logEvent(getName() + " is assigned to " + patient.getName() + ".");

        state = DoctorState.ACQUIRING_ROOM;
        hospital.logEvent(getName() + " is trying to acquire a treatment room.");

        boolean roomAcquired = treatmentRooms.tryAcquireRoom(ROOM_ACQUIRE_TIMEOUT_MS);

        if (roomAcquired) {
            hospital.logEvent(getName() + " acquired a treatment room.");

            try {
                treatPatient(patient);
            } finally {
                hospital.logEvent(getName() + " released the treatment room.");
                treatmentRooms.releaseRoom();
            }

            state = DoctorState.IDLE;
            hospital.logEvent(getName() + " is now available.");
            patient.notifyTreatmentFinished();

        } else {
            hospital.logEvent(getName() + " could not acquire a treatment room.");
            hospital.logEvent(getName() + " is sending " + patient.getName() + " back to the waiting room.");

            returnPatientToWaitingRoom(patient);

            state = DoctorState.IDLE;
            hospital.logEvent(getName() + " is now available.");
        }

        currentPatient = null;
    }

    private void treatPatient(Patient patient) throws InterruptedException {
        state = DoctorState.TREATING;
        patient.setState(PatientState.IN_TREATMENT);
        hospital.logEvent(getName() + " begins treating " + patient.getName() + ".");

        int treatmentTime = ThreadLocalRandom.current().nextInt(2000, 5001);
        Thread.sleep(treatmentTime);

        hospital.logEvent(getName() + " finished treating " + patient.getName() + ".");
    }

    private void returnPatientToWaitingRoom(Patient patient) {
        waitingRoom.enterWaitingRoom(patient);
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
