enum Direction {
    LEFT, RIGHT, UP, DOWN;
}

public class Taxi {
    int id;
    Coordinate position;
    boolean isStanding; //standing or not
    boolean available; //available to take a ride request
    Direction direction;
    Ride ride;
    boolean drivingToStart; //if taxi drives only to start point
    boolean drivingByRequest; //if taxi drives from start to end

    public Taxi(Coordinate coordinate, int i) {
        this.position = new Coordinate(coordinate);
        this.isStanding = true;
        this.available = true;
        this.id = i;
        this.direction = Direction.RIGHT;
        this.ride = null;
        this.drivingByRequest = false;
        this.drivingToStart = false;

    }

    @Override
    public String toString() {
        return
                position +
                (isStanding ? " (standing)" : " (driving)");
    }
}
