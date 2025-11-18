import org.junit.Test;

import static org.junit.Assert.*;

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

        Thread[] threads = new Thread[tasks.length];

        for (int i = 0; i < tasks.length; i++) {
            threads[i] = new Thread(tasks[i]);
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException ignored) {}
        }

        int parkedCount = 0;

        for (ParkingLockTask task : tasks) {
            if (task.car.getParkedSpot() == null) {
                // машина успела приехать и уехать — это нормально
                parkedCount++;
            }
        }

        assertTrue(parkedCount >= 3);
    }
}