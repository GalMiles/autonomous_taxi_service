import java.text.DecimalFormat;
import java.util.Random;

public class Coordinate {
    double x, y;

    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate(Coordinate newCoordinate) {
        this.x = newCoordinate.x;
        this.y = newCoordinate.y;
    }

    public static Coordinate generateRandomCoordinate() {
        Random r = new Random();
        double low = 0;
        double high = 20;
        double x = r.nextDouble(high-low) + low;
        double y = r.nextDouble(high-low) + low;
        return new Coordinate(x, y);
    }

    @Override
    public String toString() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return
                decimalFormat.format(x)+ " Km, " + decimalFormat.format(y)+" Km";
    }
}
