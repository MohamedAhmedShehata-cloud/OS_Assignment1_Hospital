package Classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point: reads input, creates and starts all threads, prints events.
 *
 * OWNER: Youssef
 *
 * CONTRACT other classes rely on:
 *  - logEvent(...) must be thread-safe (synchronized) so lines never interleave.
 *  - simulationActive must be volatile; isSimulationActive() just returns it.
 *  - onPatientLeft(...) counts leaving patients under completionMutex. When the
 *    count reaches numPatients it must, in this order:
 *        simulationActive = false;
 *        log "All patients have been treated.";
 *        waitingRoom.wakeUpWaitingDoctors(numDoctors);
 *        log "Simulation completed.";
 *  - main must wait (join) for all threads before exiting.
 *  - Print "Patients arriving:" and the list "P1 P2 ... Pn" before starting patients.
 */
public class Hospital {

    private int waitingRoomCapacity;
    private int numDoctors;
    private int numPatients;
    private int treatedPatientsCount;
    private volatile boolean simulationActive = true;

    private WaitingRoom waitingRoom;
    private TreatmentRoomManager treatmentRooms;
    private final List<Doctor> doctors = new ArrayList<>();
    private final List<Patient> patients = new ArrayList<>();
    private final Semaphore completionMutex = new Semaphore(1);
    private final List<Thread> allThreads = new ArrayList<>();

    public static void main(String[] args) {
        Hospital hospital = new Hospital();
        hospital.readInputs();
        hospital.startSimulation();
        for (Thread thread : hospital.allThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /** Reads capacity, number of doctors and number of patients (validate > 0). */
    public void readInputs() {
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.print("Enter The Waiting Room Capacity: ");
            waitingRoomCapacity = scanner.nextInt();
            if (waitingRoomCapacity <= 0) {
                System.out.println("Waiting room capacity must be greater than Zero");
            }
        } while (waitingRoomCapacity <= 0);

        do {
            System.out.print("Enter The Number of doctors: ");
            numDoctors = scanner.nextInt();
            if (numDoctors <= 0) {
                System.out.println("Number of Doctors must be greater than Zero");
            }
        } while (numDoctors <= 0);

        do {
            System.out.print("Enter The Number of patients: ");
            numPatients = scanner.nextInt();
            if (numPatients <= 0) {
                System.out.println("Number of patients must be greater than Zero");
            }
        } while (numPatients <= 0);
    }

    /** Creates WaitingRoom, TreatmentRoomManager, doctors, patients; starts threads. */
    public void startSimulation() {
        treatedPatientsCount = 0;
        simulationActive = true;
        waitingRoom = new WaitingRoom(waitingRoomCapacity, this);
        treatmentRooms = new TreatmentRoomManager();
        for (int i = 0; i < numDoctors; i++) {
            Doctor doctor = new Doctor(i + 1, waitingRoom, treatmentRooms, this);
            doctors.add(doctor);
            Thread doctorThread = new Thread(doctor);
            allThreads.add(doctorThread);
            doctorThread.start();
        }
        logEvent("Patients arriving:");
        StringBuilder arrivingList = new StringBuilder();
        for (int i = 1; i <= numPatients; i++) {
            if (i > 1) {
                arrivingList.append(" ");
            }
            arrivingList.append("P").append(i);
        }
        logEvent(arrivingList.toString());

        for (int i = 0; i < numPatients; i++) {
            Patient patient = new Patient(i + 1, waitingRoom, this);
            patients.add(patient);
            Thread patientThread = new Thread(patient);
            allThreads.add(patientThread);
            patientThread.start();
        }
    }

    public synchronized void logEvent(String message) {
        System.out.println(message);
    }

    public void onPatientLeft(Patient patient) {

        completionMutex.acquire();
        try {
            treatedPatientsCount++;
            if (treatedPatientsCount == numPatients) {
                simulationActive = false;
                logEvent("All patients have been treated.");
                waitingRoom.wakeUpWaitingDoctors(numDoctors);
                logEvent("Simulation completed.");
            }
        } finally {
            completionMutex.release();
        }
    }

    public boolean isSimulationActive() {
        return simulationActive;
    }
}