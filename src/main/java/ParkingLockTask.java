public class ParkingLockTask implements Runnable {
    Car car;
    ParkingLock lock;

    public ParkingLockTask(Car car, ParkingLock lock) {
        this.car = car;
        this.lock = lock;
    }

    @Override
    public void run() {
        boolean parked = lock.tryEnter(car);

        if (!parked) return;

        try {
            Thread.sleep((long) (Math.random() * 3000 + 2000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        lock.leave(car);
    }
}
