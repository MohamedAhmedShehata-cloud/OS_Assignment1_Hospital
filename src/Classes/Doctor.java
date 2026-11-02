package Classes;
/**
 * A doctor thread.
 *
 * OWNER: (you / Omar)
 *
 * run() loops while hospital.isSimulationActive():
 *   1. state = IDLE; patient = waitingRoom.takeNextPatient(this)
 *      (null means the simulation ended -> leave the loop)
 *   2. handlePatient(patient)
 *
 * handlePatient(p) must do, in order:
 *   - log "<doctor> is assigned to <p>."            (states: ASSIGNED, ASSIGNED_TO_DOCTOR)
 *   - log "<doctor> is trying to acquire a treatment room."
 *   - try to get a room for ROOM_ACQUIRE_TIMEOUT_MS (3 s)
 *   - success: log "acquired a treatment room." -> treatPatient(p)
 *              -> log "is now available." -> p.notifyTreatmentFinished()
 *   - failure: log "could not acquire a treatment room."
 *              -> returnPatientToWaitingRoom(p) -> log "is now available."
 *
 * treatPatient(p): log "begins treating <p>.", sleep a random time, log
 *   "finished treating <p>.", then ALWAYS release the room (log "released the
 *   treatment room." and release) even if something goes wrong.
 *
 * Important: the room must not be given back before its "released" line is
 * logged, or another doctor's "acquired" line can show up first in the output.
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
                  TreatmentRoomManager treatmentRooms, Hospital hospital) {
        this.id = id;
        this.waitingRoom = waitingRoom;
        this.treatmentRooms = treatmentRooms;
        this.hospital = hospital;
    }

    @Override
    public void run() {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    private void handlePatient(Patient patient) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    private void treatPatient(Patient patient) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    private void returnPatientToWaitingRoom(Patient patient) {
        // TODO
        throw new UnsupportedOperationException("TODO");
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