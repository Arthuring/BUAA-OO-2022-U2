import com.oocourse.elevator3.PersonRequest;

import java.util.List;

public abstract class Strategy {
    public static final Position EXIT = new Position(Building.Z, -1);
    private final PersonQueue outside;
    private final List<PersonRequest> inside;
    private int speed;

    protected Strategy(PersonQueue outside, List<PersonRequest> inside) {
        this.outside = outside;
        this.inside = inside;
    }

    public abstract Position decideTarget(Position currentPosition, Position lastPosition)
            throws InterruptedException;

    public abstract Position decideTargetInOut(Position currentPosition, Position lastPosition)
            throws InterruptedException;

    protected PersonQueue outside() {
        return outside;
    }

    protected List<PersonRequest> inside() {
        return inside;
    }

    protected void setspeed(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }
}
