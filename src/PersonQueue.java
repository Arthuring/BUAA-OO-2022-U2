import java.util.HashSet;
import java.util.List;

public abstract class PersonQueue {
    public static final RequestList EXIT = new RequestList(-1, -1, 'Z', 'Z', -1);

    public abstract void addRequest(RequestList request);

    public abstract void setEnd(boolean end);

    public abstract RequestList getFarestRequest(
            Position currentFloor, Position lastFloor,
            HashSet<Position> reachablePos) throws InterruptedException;

    public abstract RequestList getFarestRequestInOut(
            Position currentFloor, Position lastFloor,
            HashSet<Position> reachablePos) throws InterruptedException;

    public abstract List<RequestList> getInPerson(int maxNum,
                                                    Position currentPosition,
                                                    Direction direction,
                                                  HashSet<Position> reachablePos);

    public abstract boolean isEmpty();

    public abstract RequestList containSameDirection(Position currentFloor,
                                                       Direction direction,
                                                     HashSet<Position> reachablePos);
}
