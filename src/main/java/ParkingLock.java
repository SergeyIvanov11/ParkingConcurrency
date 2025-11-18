import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ParkingLock {
    private final List<ReentrantLock> spots;
    private final long maxWaitTime; // секунд ожидания
    private final ReentrantLock commonLock;
    private final Condition freeSpot;  // указывает что появилось свободное место

    public ParkingLock(int spotsNumber, int maxWaitTime) {
        this.maxWaitTime = (long) maxWaitTime * 1000;
        this.spots = new ArrayList<>();
        for (int i = 0; i < spotsNumber; i++) {
            spots.add(new ReentrantLock());
        }
        this.commonLock = new ReentrantLock(true);
        this.freeSpot = commonLock.newCondition();
    }

    public boolean tryEnter(Car car) {
        System.out.println("Машина №" + car.getNumber() + " пытается въехать на парковку...");
        long deadline = System.currentTimeMillis() + maxWaitTime;

        commonLock.lock();
        try {
            int spot = findFreeSpot(car);
            if (spot != -1) {
                return true;
            }

            while (true) {
                long timeLeft = deadline - System.currentTimeMillis();
                if (timeLeft <= 0) {
                    System.out.println("Машина №" + car.getNumber() + " не дождалась места и уехала.");
                    return false;
                }

                boolean signaled = freeSpot.await(timeLeft, TimeUnit.MILLISECONDS);
                if (!signaled) {
                    System.out.println("Машина №" + car.getNumber() + " не дождалась места и уехала.");
                    return false;
                }

                spot = findFreeSpot(car);
                if (spot != -1) {
                    return true;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            commonLock.unlock();
        }
    }

    private int findFreeSpot(Car car) {
        for (int i = 0; i < spots.size(); i++) {
            ReentrantLock spot = spots.get(i);
            if (spot.tryLock()) {
                car.setParkedSpot(i);
                System.out.println("Машина №" + car.getNumber() + " заняла место " + (i + 1));
                return i;
            }
        }
        return -1;
    }

    public void leave(Car car) {
        Integer spotIndex = car.getParkedSpot();
        if (spotIndex == null) return;

        ReentrantLock spot = spots.get(spotIndex);

        try {
            spot.unlock();
            System.out.println("Машина №" + car.getNumber() + " покинула место " + (spotIndex + 1));
        } catch (IllegalMonitorStateException e) {
            e.printStackTrace();
        }

        commonLock.lock();
        try {
            car.setParkedSpot(null);
            freeSpot.signal(); // сигнал одной ожидающей машине
        } finally {
            commonLock.unlock();
        }
    }
}
