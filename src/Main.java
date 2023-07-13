import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        System.out.printf("Welcome to my Autonomous Taxi Service!%n");
        System.out.printf("====================================%n");
        System.out.printf("To start the simulation press 1.%n");
        System.out.printf("To exit press 0.%n");

        Scanner scanner = new Scanner(System.in);
        int input = scanner.nextInt();

        Simulation simulation = new Simulation();

        while(input == 1) {
            //1 - initial taxis
            simulation.initialTaxis();
            simulation.startSimulator();

            input = scanner.nextInt();
        }
        simulation.stopSimulator();
        System.out.println("You have exited the simulation.");
        return;
    }
}