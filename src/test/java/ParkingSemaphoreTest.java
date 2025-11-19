import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParkingSemaphoreTest {
    @Test
    public void testSingleCarEntersSuccessfully() throws InterruptedException {
        ParkingSemaphore parking = new ParkingSemaphore(1, 3); // одно место, 3 секунды ожидания
        Car car = new Car(1);

        Thread t = new Thread(new ParkingSemaphoreTask(car, parking));
        t.start();
        t.join();

        assertEquals(1, parking.semaphore.availablePermits());
    }

    @Test
    public void testCarCannotEnterWhenNoSpots() throws InterruptedException {
        ParkingSemaphore parking = new ParkingSemaphore(1, 1);

        Car c1 = new Car(1);
        Car c2 = new Car(2);

        Thread t1 = new Thread(new ParkingSemaphoreTask(c1, parking));
        Thread t2 = new Thread(new ParkingSemaphoreTask(c2, parking));

        t1.start();
        Thread.sleep(50); // гарантируем, что первая машина заняла место
        t2.start();

        t1.join();
        t2.join();

        // Доступные места снова 1 — машина 1 вошла и вышла,
        // машина 2 не вошла.
        assertEquals(1, parking.semaphore.availablePermits());
    }
}