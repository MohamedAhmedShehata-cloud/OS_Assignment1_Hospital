package Classes;

/**
 * Bounded buffer (circular queue) of patients waiting for a doctor.
 * <p>
 * OWNER: (you)
 * <p>
 * Producer-consumer with three semaphores:
 * - emptySeats        : free seats      (patients wait on it)
 * - availablePatients : occupied seats  (doctors wait on it)
 * - mutex             : protects buffer / head / tail / count
 * <p>
 * CONTRACT other classes rely on:
 * - enterWaitingRoom(p) is called by a new patient AND by a doctor who sends a
 * patient back. It blocks while the room is full. It logs:
 * "The waiting room is full."  +  "<name> is waiting outside for an available seat."
 * (only if no seat is free), and "<name> entered the waiting room." when seated.
 * It also sets the patient state (WAITING_OUTSIDE / IN_WAITING_ROOM).
 * Tip: tryAcquire(0) on emptySeats lets you check "full?" and reserve the seat atomically.
 * - takeNextPatient(doctor) blocks while the room is empty (the doctor is idle).
 * It returns null if the simulation ended while waiting.
 * - wakeUpWaitingDoctors(n) is called once at the end so idle doctors can finish.
 */
public class WaitingRoom {

    private final int capacity;
    private final Patient[] buffer;
    private int head;
    private int tail;
    private int count;

    private final Semaphore mutex;
    private final Semaphore emptySeats;
    private final Semaphore availablePatients;
    private final Hospital hospital;

    public WaitingRoom(int capacity, Hospital hospital) {
        this.capacity = capacity;
        this.hospital = hospital;
        this.buffer = new Patient[capacity];

        this.head = 0;
        this.tail = 0;
        this.count = 0;

        this.mutex = new Semaphore(1);
        this.emptySeats = new Semaphore(capacity);
        this.availablePatients = new Semaphore(0);
    }

    /**
     * Producer side: puts a patient in the buffer (blocks while the room is full).
     */
    public void enterWaitingRoom(Patient patient) {
        if (emptySeats.tryAcquire(0)) {
            patient.setState(PatientState.WAITING_OUTSIDE);
            emptySeats.acquire();
            // need the logs
        }
        patient.setState(PatientState.IN_WAITING_ROOM);
        // log
        mutex.acquire();
        try {
            buffer[tail] = patient;
            tail = (tail + 1) % capacity;
            count++;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            mutex.release();
        }
        availablePatients.release();
    }

    /**
     * Consumer side: removes and returns the next patient (blocks while empty).
     */
    public Patient takeNextPatient(Doctor doctor) {
        availablePatients.acquire();
        Patient patient = null;
        mutex.acquire();

        try {
            if (!hospital.isSimulationActive() && count == 0) {
                return null;
            }
            patient = buffer[head];
            buffer[head] = null;
            head = (head + 1) % capacity;
            count--;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            mutex.release();
        }
        emptySeats.release();
        return patient;
    }

    /**
     * Wakes every idle doctor at the end of the simulation.
     */
    public void wakeUpWaitingDoctors(int numDoctors) {
        for (int i = 0; i < numDoctors; i++) {
            availablePatients.release();
        }
    }
}