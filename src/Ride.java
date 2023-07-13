public class Ride {
    Coordinate start;
    Coordinate end;

    public Ride(Coordinate start, Coordinate end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "Ride{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
