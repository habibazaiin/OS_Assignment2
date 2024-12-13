import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ParkingSimulation {
    private static final int TOTAL_SPOTS = 4;
    private static final Semaphore parkingSpots = new Semaphore(TOTAL_SPOTS);
    private static int totalCarsServed = 0;
    private static int[] carsServedAtGates = new int[3];

    public static void main(String[] args) {
        String inputFile = "input.txt";
        String outputFile = "output.txt";

        List<Car> cars = CarLoader.loadCars(inputFile, parkingSpots, TOTAL_SPOTS);
        List<Thread> carThreads = new ArrayList<>();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (Car car : cars) {
                car.setWriter(writer);
                Thread carThread = new Thread(car);
                carThreads.add(carThread);
                carThread.start();
            }

            for (Thread carThread : carThreads) {
                carThread.join();
            }

            writer.write("\nTotal Cars Served: " + totalCarsServed + "\n");
            writer.write("Current Cars in Parking: " +(TOTAL_SPOTS - parkingSpots.availablePermits()) + "\n");
            writer.write("Details:\n");
            for (int i = 0; i < carsServedAtGates.length; i++) {
                writer.write("- Gate " + (i + 1) + " served " + carsServedAtGates[i] + " cars.\n");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void incrementCarCount(int gate) {
        totalCarsServed++;
        carsServedAtGates[gate]++;
    }
}
