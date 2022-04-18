import com.oocourse.elevator3.PersonRequest;

import java.util.List;

public class LookStrategyVertical extends Strategy {
    protected LookStrategyVertical(PersonQueue outside, List<PersonRequest> inside) {
        super(outside, inside);
        super.setspeed(400);
    }

    @Override
    public Position decideTarget(Position currentFloor, Position lastFloor)
            throws InterruptedException {
        if (inside().isEmpty()) {
            PersonRequest farest = outside().getFarestRequest(currentFloor, lastFloor);
            if (farest.equals(PersonQueue.EXIT)) {
                return EXIT;
            } else {
                return new Position(Building.valueOf(String.valueOf(farest.getFromBuilding()))
                        , farest.getFromFloor());
            }
        } else {
            Direction direction = new Direction(currentFloor, lastFloor);
            int farestFloor;
            if (direction.getVertical() > 0) {
                farestFloor = 0;
                for (PersonRequest r : inside()) {
                    if (r.getToFloor() > farestFloor) {
                        farestFloor = r.getToFloor();
                    }
                }
            } else {
                farestFloor = 11;
                for (PersonRequest r : inside()) {
                    if (r.getToFloor() < farestFloor) {
                        farestFloor = r.getToFloor();
                    }
                }
            }
            return new Position(currentFloor.getBuilding(), farestFloor);
        }
    }

    public Position decideTargetInOut(Position currentPosition, Position lastPosition)
            throws InterruptedException {
        if (inside().isEmpty()) {
            PersonRequest farest = outside().getFarestRequestInOut(currentPosition, lastPosition);
            if (farest.equals(PersonQueueVertical.EXIT)) {
                return EXIT;
            } else {
                Building building = Building.
                        valueOf(String.valueOf(farest.getFromBuilding()));
                if (farest.getFromFloor() != currentPosition.getFloor()) {
                    return new Position(building, farest.getFromFloor());
                } else {
                    return new Position(building, farest.getToFloor());
                }
            }
        } else {
            Direction direction = new Direction(currentPosition, lastPosition);
            int farestFloor;
            if (direction.getVertical() > 0) {
                farestFloor = 0;
                for (PersonRequest r : inside()) {
                    if (r.getToFloor() > farestFloor) {
                        farestFloor = r.getToFloor();
                    }
                }
            } else {
                farestFloor = 11;
                for (PersonRequest r : inside()) {
                    if (r.getToFloor() < farestFloor) {
                        farestFloor = r.getToFloor();
                    }
                }
            }
            return new Position(currentPosition.getBuilding(), farestFloor);
        }
    }
}
