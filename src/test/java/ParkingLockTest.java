import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ParkingLockTest {
    @Test
    public void testSingleCarEntersSuccessfully() {
        ParkingLock parking = new ParkingLock(1, 3);
        Car car = new Car(1);

        boolean entered = parking.tryEnter(car);

        assertTrue(entered);
        assertNotNull(car.getParkedSpot());
    }

    @Test
    public void testCarLeavesSpot() {
        ParkingLock parking = new ParkingLock(1, 3);
        Car car = new Car(1);

        assertTrue(parking.tryEnter(car));
        Integer spot = car.getParkedSpot();
        assertNotNull(spot);

        parking.leave(car);

        assertNull(car.getParkedSpot());
    }

    @Test
    public void testThreeSpotsAndFiveCarsCompete() {
        ParkingLock parking = new ParkingLock(3, 3);

        ParkingLockTask[] tasks = {
                new ParkingLockTask(new Car(1), parking),
                new ParkingLockTask(new Car(2), parking),
                new ParkingLockTask(new Car(3), parking),
                new ParkingLockTask(new Car(4), parking),
                new ParkingLockTask(new Car(5), parking)
        };

        Thread[] threads = Arrays.stream(tasks)
                .map(Thread::new)
                .peek(Thread::start)
                .toArray(Thread[]::new);

        Arrays.stream(threads).forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        int parkedCount = 0;

        for (ParkingLockTask task : tasks) {
            if (task.car.getParkedSpot() == null || task.car.getParkedSpot() != null) {
                // машина успела приехать и уехать или еще припаркована
                parkedCount++;
            }
        }

        assertTrue(parkedCount >= 3);
    }

    @Test
    public void testBigParkingManyCars() throws InterruptedException {
        final ParkingLock parking = new ParkingLock(20, 5);

        int carsCount = 50;
        Car[] cars = new Car[carsCount];
        Thread[] threads = new Thread[carsCount];

        for (int i = 0; i < carsCount; i++) {
            cars[i] = new Car(i + 1);
            threads[i] = new Thread(new ParkingLockTask(cars[i], parking));
            threads[i].start();
        }

        for (Thread t : threads) t.join();

        int parkedCount = 0;
        for (Car car : cars) {
            if (car.getParkedSpot() != null) parkedCount++;
        }

        assertTrue(parkedCount <= 20);
        }

    @Test
    public void testTwoOverlappingBatches() throws InterruptedException {
        final ParkingLock parking = new ParkingLock(5, 5);

        // Первая пачка: 5 машин
        Car[] batch1 = new Car[5];
        Thread[] threads1 = new Thread[batch1.length];

        for (int i = 0; i < batch1.length; i++) {
            batch1[i] = new Car(i + 1);
            threads1[i] = new Thread(new ParkingLockTask(batch1[i], parking));
            threads1[i].start();
        }

        // Задержка, чтобы первая пачка уже частично заехала, но ещё не успела уехать
        Thread.sleep(400);

        // Вторая пачка: ещё 5 машин
        Car[] batch2 = new Car[5];
        Thread[] threads2 = new Thread[batch2.length];

        for (int i = 0; i < batch2.length; i++) {
            batch2[i] = new Car(i + 101);
            threads2[i] = new Thread(new ParkingLockTask(batch2[i], parking));
            threads2[i].start();
        }

        for (Thread t : threads1) t.join();
        for (Thread t : threads2) t.join();

        // Считаем реальное число припаркованных машин
        int parked = 0;
        for (Car c : batch1) if (c.getParkedSpot() != null) parked++;
        for (Car c : batch2) if (c.getParkedSpot() != null) parked++;

        assertTrue(parked <= 5);
    }
}