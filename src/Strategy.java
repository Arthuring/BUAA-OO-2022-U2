import com.oocourse.elevator3.PersonRequest;

import java.util.HashSet;
import java.util.List;

public abstract class Strategy {
    public static final Position EXIT = new Position(Building.Z, -1);
    private final PersonQueue outside;
    private final List<RequestList> inside;

    protected Strategy(PersonQueue outside, List<RequestList> inside) {
        this.outside = outside;
        this.inside = inside;
    }

    public abstract Position decideTarget(Position currentPosition, Position lastPosition,
                                          HashSet<Position> reachablePos)
            throws InterruptedException;

    public abstract Position decideTargetInOut(Position currentPosition, Position lastPosition,
                                               HashSet<Position> reachablePos)
            throws InterruptedException;

    protected PersonQueue outside() {
        return outside;
    }

    protected List<RequestList> inside() {
        return inside;
    }

}
