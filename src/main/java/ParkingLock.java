import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ParkingLock {
    private final Lock lock = new ReentrantLock(true);
    private final Condition spotAvailable = lock.newCondition();
    private final AtomicInteger freeSpots;
    private final long maxWaitTime; // секунд ожидания

    public ParkingLock(int spots, int maxWaitTime) {
        this.freeSpots = new AtomicInteger(spots);
        this.maxWaitTime = (long) maxWaitTime * 1000;
    }

    public boolean tryEnter(Car car) {
        System.out.println("Машина №" + car.getNumber() + " пытается въехать на парковку...");

        long startTime = System.currentTimeMillis();

        lock.lock();

        try {
            while (freeSpots.get() == 0) {
                long elapsed = System.currentTimeMillis() - startTime;
                long remaining = maxWaitTime - elapsed;

                if (remaining <= 0) {
                    System.out.println("Машина " + car.getNumber() + " не дождалась места и уехала.");
                    return false;
                }

                try {
                    // ждём интервалами, периодически проверяем условие
                    spotAvailable.await(Math.min(remaining, 500), TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Машина " + car.getNumber() + " была прервана при ожидании.");
                    return false;
                }
            }
            freeSpots.decrementAndGet();
            System.out.println("Машина №" + car.getNumber() + " заехала на парковку. Свободных мест: " + freeSpots);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public void leave(Car car) {
        lock.lock();
        try {
            freeSpots.incrementAndGet();
            System.out.println("Машина №" + car.getNumber() + " покинула парковку. Свободных мест: " + freeSpots);
            spotAvailable.signal(); // будим одну ожидающую машину
        } finally {
            lock.unlock();
        }
    }

}
