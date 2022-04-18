import com.oocourse.elevator3.PersonRequest;

import java.util.List;

public abstract class PersonQueue {
    public static final PersonRequest EXIT = new PersonRequest(-1, -1, 'Z', 'Z', -1);

    public abstract void addRequest(PersonRequest request);

    public abstract void setEnd(boolean end);

    public abstract PersonRequest getFarestRequest(
            Position currentFloor, Position lastFloor) throws InterruptedException;

    public abstract PersonRequest getFarestRequestInOut(
            Position currentFloor, Position lastFloor) throws InterruptedException;

    public abstract List<PersonRequest> getInPerson(int maxNum,
                                                    Position currentPosition,
                                                    Direction direction);

    public abstract boolean isEmpty();

    public abstract PersonRequest containSameDirection(Position currentFloor,
                                                       Direction direction);
}
