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

    public static void main(String[] args) {
        // TODO: new Hospital(), readInputs(), startSimulation()
        Hospital hospital = new Hospital();
        hospital.readInputs();
        hospital.startSimulation();

        throw new UnsupportedOperationException("TODO");
    } 

    /** Reads capacity, number of doctors and number of patients (validate > 0). */
    public void readInputs() {
        do{ Scanner input = new Scanner(System.in);
          System.out.print("Enter The Waiting Room Capacity: ")
          waitingRoomCapacity = input.nextInt();          
          
          if (waitingRoomCapacity<=0)
          System.out.println("waiting room capacity must be greater than Zero")
          
          }where(waitingRoomCapacity<=0)
      
        do{System.out.print("Enter The Number of doctors: ")
            numDoctors = input.nextInt();

             if (numDoctors<=0)
          System.out.println("Number of Doctors must be greater than Zero")
          
          }where(numDoctors<=0)                            

        do{
           System.out.print("Enter The Number of patients: ")
              numPatients = input.nextInt();
              
               if (numPatients<=0)
          System.out.println("Number of patients must be greater than Zero")
          
          }where(numPatients<=0)
      

        throw new UnsupportedOperationException("TODO");
    }

    /** Creates WaitingRoom, TreatmentRoomManager, doctors, patients; starts threads. */
    public void startSimulation() {
        treatedPatientsCount=0;
        simulationActive = true;
        waitingRoom = new waitingRoom(waitingRoomCapacity,this);
        treatmentRooms= new TreatmentRoomManager();

        List <Thread>thread=new ArrayList<>();
        for(int i=0;i<numDoctors;i++)
        {
            Doctor doctor=new Doctor(i+1,waitingRoom,treatmentRooms,this);
            doctors.add(doctor);
            Thread doctorthread = new Thread(doctor);
        }



        throw new UnsupportedOperationException("TODO");
    }

    public synchronized void logEvent(String message) {
        System.out.println(message);
    }

    public void onPatientLeft(Patient patient) {
        // TODO (see the contract in the class comment)
        throw new UnsupportedOperationException("TODO");
    }

    public boolean isSimulationActive() {
        return simulationActive;
    }
}
