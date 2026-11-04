
package classes;

import java.util.concurrent.ThreadLocalRandom;

/*
 A doctor thread.
 OWNER: Omar / Mohamed
 run() loops while the simulation is active:
 1. Wait for the next patient.
 2. Handle the patient.

 handlePatient():
 1. Log patient assignment.
 2. Try to acquire a treatment room (3 seconds).
 3. Treat the patient if successful.
 4. Return the patient to the waiting room if unsuccessful.

 treatPatient():
 1. Log treatment start.
 2. Sleep for a random duration.
 3. Log treatment completion.
 4. Always release the treatment room.
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
                // Doctor defautly set to idle
                state = DoctorState.IDLE;

                // Take the next available patient
                Patient patient = waitingRoom.takeNextPatient(this);

                // Null = simulation ended
                if (patient == null) {
                    break;
                }

                currentPatient = patient;
                // Handle the assigned patient
                handlePatient(patient);
            } catch (InterruptedException e) {
                // Restore interrupted status and stop the doctor
                Thread.currentThread().interrupt();
                break;
            }
        }

        state = DoctorState.IDLE;
        currentPatient = null;
    }

    private void handlePatient(Patient patient)
            throws InterruptedException {

        // Patient assigned to doctor
        state = DoctorState.ASSIGNED;
        patient.setState(PatientState.ASSIGNED_TO_DOCTOR);

        hospital.logEvent(
                getName() + " is assigned to" + patient.getName()
        );

        // Doctor starts trying to acquire a treatment room
        state = DoctorState.ACQUIRING_ROOM;

        hospital.logEvent(
                getName() + " Tries to acquire a treatment room"
        );

        // Try acquiring a room with a 3-second timeout
        boolean roomAcquired =
                treatmentRooms.tryAcquireRoom(ROOM_ACQUIRE_TIMEOUT_MS);

        if (roomAcquired) {
            // Successfully acquired a treatment room
            hospital.logEvent(
                    getName() + " acquired a treatment room"
            );

            try {
                // Treat the patient
                treatPatient(patient);
            } finally {
                // Log the release BEFORE returning the room semaphore.
                hospital.logEvent(
                        getName() + " released the treatment room"
                );
                treatmentRooms.releaseRoom();
            }

            // Doctor is now available
            state = DoctorState.IDLE;

            hospital.logEvent(
                    getName() + " is now available"
            );

            // Notify patient that treatment has finished
            patient.notifyTreatmentFinished();

        } else {

            // No treatment room was available within 3 seconds
            hospital.logEvent(
                    getName() + " could not acquire a treatment room"
            );

            // Return patient to waiting room
            returnPatientToWaitingRoom(patient);

            // Doctor is now available
            state = DoctorState.IDLE;

            hospital.logEvent(
                    getName() + " is now available"
            );
        }

        currentPatient = null;
    }

    private void treatPatient(Patient patient)
            throws InterruptedException {

        // Update states
        state = DoctorState.TREATING;
        patient.setState(PatientState.IN_TREATMENT);

        // Log treatment start
        hospital.logEvent(
                getName() + " begins treating " + patient.getName()
        );

        // Generate random treatment duration (2 to 5 seconds)
        int treatmentTime = ThreadLocalRandom.current()
                .nextInt(2000, 5001);

        // Simulate treatment
        Thread.sleep(treatmentTime);

        // Log treatment completion
        hospital.logEvent(
                getName() + " finished treating " + patient.getName()
        );
    }

    private void returnPatientToWaitingRoom(Patient patient) {
        // Return patient to the waiting room queue
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
