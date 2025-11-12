import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ParkingSemaphore {
    private final Semaphore semaphore;
    private final int maxWaitTime; // секунд ожидания

    public ParkingSemaphore(int spots, int maxWaitTime) {
        this.semaphore = new Semaphore(spots, true);
        this.maxWaitTime = maxWaitTime;
    }

    public boolean tryEnter(Car car) {
        System.out.println("Машина №" + car.getNumber() + " пытается въехать на парковку...");
        try {
            if (semaphore.tryAcquire(maxWaitTime, TimeUnit.SECONDS)) {
                System.out.println("Машина №" + car.getNumber() + " заехала на парковку");
                return true;
            } else {
                System.out.println("Машина №" + car.getNumber() + " не дождалась места и уехала");
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void leave(Car car) {
        semaphore.release();
        System.out.println("Машина №" + car.getNumber() + " покинула парковку. Свободных мест: " + semaphore.availablePermits());
    }
}
