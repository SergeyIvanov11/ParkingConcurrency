public class ParkingSemaphoreTask implements Runnable {
    private Car car;
    private ParkingSemaphore semaphore;

    public ParkingSemaphoreTask(Car car, ParkingSemaphore semaphore) {
        this.car = car;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        if (semaphore.tryEnter(car)) {
            try {
                Thread.sleep((long) (1000 + Math.random() * 5000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                semaphore.leave(car);
            }
        }
    }
}
