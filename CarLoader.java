import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;

public class CarLoader {
    public static List<Car> loadCars(String filename, Semaphore parkingSpots, int totalSpots) {
        List<Car> cars = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");
                String gate = parts[0];
                int id = Integer.parseInt(parts[1].split(" ")[1]);
                int arrivalTime = Integer.parseInt(parts[2].split(" ")[1]);
                int parkDuration = Integer.parseInt(parts[3].split(" ")[1]);
                cars.add(new Car(gate, id, arrivalTime, parkDuration, parkingSpots, totalSpots));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cars;
    }
}
