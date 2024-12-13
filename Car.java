import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.Semaphore;

public class Car implements Runnable {
    private final String gate;
    private final int id;
    private final int arrivalTime;
    private final int parkDuration;
    private BufferedWriter writer;
    private final Semaphore parkingSpots;
    private final int totalSpots;
    private long waitingTime = 0;

    public Car(String gate, int id, int arrivalTime, int parkDuration, Semaphore parkingSpots, int totalSpots) {
        this.gate = gate;
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.parkDuration = parkDuration;
        this.parkingSpots = parkingSpots;
        this.totalSpots = totalSpots;
    }

    public void setWriter(BufferedWriter writer) {
        this.writer = writer;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(arrivalTime * 1000);
            log("Car " + id + " from " + gate + " arrived at time " + arrivalTime);

            long waitStart = System.currentTimeMillis();

            if (parkingSpots.tryAcquire()) {
                log("Car " + id + " from " + gate + " parked " + "(Parking Status: " +
                        (totalSpots - parkingSpots.availablePermits()) + " spots occupied)");
                Thread.sleep(parkDuration * 1000);
                log("Car " + id + " from " + gate + " left after " + parkDuration +
                        " units of time. (Parking Status: " + (totalSpots - parkingSpots.availablePermits() - 1) + " spots occupied)");
                parkingSpots.release();
                ParkingSimulation.incrementCarCount(getGateIndex(gate));
            } else {
                log("Car " + id + " from " + gate + " waiting for a spot.");
                parkingSpots.acquire();
                waitingTime = ((System.currentTimeMillis() - waitStart) / 1000 )+ 1;
                log("Car " + id + " from " + gate + " parked after waiting " + waitingTime + " units of time. (Parking Status: " +
                        (totalSpots - parkingSpots.availablePermits()) + " spots occupied)");
                Thread.sleep(parkDuration * 1000);
                log("Car " + id + " from " + gate + " left after " + parkDuration +
                        " units of time. (Parking Status: " + (totalSpots - parkingSpots.availablePermits() - 1) + " spots occupied)");
                parkingSpots.release();
                ParkingSimulation.incrementCarCount(getGateIndex(gate));
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private void log(String message) throws IOException {
        synchronized (writer) {
            writer.write(message);
            writer.newLine();
            writer.flush();
            System.out.println(message);
        }
    }

    private int getGateIndex(String gate) {
        switch (gate) {
            case "Gate 1": return 0;
            case "Gate 2": return 1;
            case "Gate 3": return 2;
            default: return -1;
        }
    }
}
