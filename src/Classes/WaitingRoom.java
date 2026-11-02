package Classes;

/**
 * Bounded buffer (circular queue) of patients waiting for a doctor.
 *
 * <p>Classic producer-consumer synchronization with three semaphores:
 * <ul>
 *   <li>{@code emptySeats}        - free seats; patients (producers) wait on it</li>
 *   <li>{@code availablePatients} - occupied seats; doctors (consumers) wait on it</li>
 *   <li>{@code mutex}             - protects the buffer indices and the count</li>
 * </ul>
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
        if (capacity <= 0) {
            throw new IllegalArgumentException("Waiting room capacity must be positive");
        }
        this.capacity = capacity;
        this.hospital = hospital;
        this.buffer = new Patient[capacity];
        this.mutex = new Semaphore(1);
        this.emptySeats = new Semaphore(capacity);
        this.availablePatients = new Semaphore(0);
    }

    /**
     * Producer side. Used both by a newly arrived patient and by a doctor who
     * sends a patient back. Blocks outside the room while no seat is free.
     */
    public void enterWaitingRoom(Patient patient) {
        // Non-blocking attempt keeps the "is it full?" check and the
        // seat reservation atomic, so the log can never lie.
        if (!emptySeats.tryAcquire(0)) {
            patient.setState(PatientState.WAITING_OUTSIDE);
            hospital.logEvent("The waiting room is full.");
            hospital.logEvent(patient.getName() + " is waiting outside for an available seat.");
            emptySeats.acquire();
        }

        mutex.acquire();
        try {
            buffer[tail] = patient;
            tail = (tail + 1) % capacity;
            count++;
            patient.setState(PatientState.IN_WAITING_ROOM);
            hospital.logEvent(patient.getName() + " entered the waiting room.");
        } finally {
            mutex.release();
        }

        availablePatients.release();
    }

    /**
     * Consumer side. Blocks (the doctor is idle) until a patient is available.
     *
     * @return the next patient, or {@code null} if the simulation has ended
     */
    public Patient takeNextPatient(Doctor doctor) {
        availablePatients.acquire();

        if (!hospital.isSimulationActive()) {
            return null;
        }

        Patient patient;
        mutex.acquire();
        try {
            patient = buffer[head];
            buffer[head] = null;
            head = (head + 1) % capacity;
            count--;
        } finally {
            mutex.release();
        }

        emptySeats.release();
        return patient;
    }

    /**
     * Called once when the simulation ends: wakes every idle doctor so it can
     * see that the simulation is over and terminate.
     */
    public void wakeUpWaitingDoctors(int numDoctors) {
        for (int i = 0; i < numDoctors; i++) {
            availablePatients.release();
        }
    }
}
