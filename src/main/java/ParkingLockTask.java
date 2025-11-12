public class ParkingLockTask implements Runnable {
    private Car car;
    private ParkingLock lock;

    public ParkingLockTask(Car car, ParkingLock lock) {
        this.car = car;
        this.lock = lock;
    }

    @Override
    public void run() {
        if (lock.tryEnter(car)) {
            try {
                Thread.sleep((long) (1000 + Math.random() * 5000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.leave(car);
            }
        }
    }
}
