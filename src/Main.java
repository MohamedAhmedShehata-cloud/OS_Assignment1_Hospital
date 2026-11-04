import classes.Hospital;

public class Main {
    public static void main(String[] args) {
        Hospital hospital = new Hospital();
        hospital.readInputs();
        hospital.startSimulation();
        hospital.waitForCompletion();
    }
}
