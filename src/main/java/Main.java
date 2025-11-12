import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) throws UnsupportedEncodingException {
        int totalCars = 100;
        int parkingSpots = 5;
        int maxWaitTime = 5;

        System.setOut(new PrintStream(System.out, true, "UTF-8"));

        ParkingSemaphore semaphore = new ParkingSemaphore(parkingSpots, maxWaitTime);
        ParkingLock lock = new ParkingLock(parkingSpots, maxWaitTime);

        Queue<Car> carQueue = new LinkedBlockingQueue<>();

        for (int i = 1; i <= totalCars; i++) {
            carQueue.offer(new Car(i));
        }

        while (!carQueue.isEmpty()) {
            try {
                Car car = carQueue.poll();

                //   Thread thread = new Thread(new ParkingSemaphoreTask(car, semaphore));
                Thread thread = new Thread(new ParkingLockTask(car, lock));

                thread.start();
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
