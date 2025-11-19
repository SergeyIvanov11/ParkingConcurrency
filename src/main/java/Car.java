
public class Car {
    private int number;
    private Integer parkedSpot = null;

    public Car(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public Integer getParkedSpot() {
        return parkedSpot;
    }

    public void setParkedSpot(Integer parkedSpot) {
        this.parkedSpot = parkedSpot;
    }
}
