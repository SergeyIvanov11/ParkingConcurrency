[Бизнес-центр](https://github.com/SergeyIvanov11/ParkingConcurrency/blob/master/src/main/java/Main.java) имеет ограниченное количество парковочных мест (например, 5). Посетители приезжают в разное время. Если все места заняты, машина ожидает освобождения одного из мест, но не дольше чем 5 секунд. Как только место освобождается, одна из ожидающих машин занимает его Парковка не может быть перегружена — нельзя въехать, если нет свободного места.

[Реализация с Semaphore](https://github.com/SergeyIvanov11/ParkingConcurrency/blob/master/src/main/java/ParkingSemaphore.java)

[Реализация с ReentrantLock](https://github.com/SergeyIvanov11/ParkingConcurrency/blob/master/src/main/java/ParkingLock.java)
