import java.util.HashSet;
import java.util.List;

public class LookStrategyVertical extends Strategy {
    protected LookStrategyVertical(PersonQueue outside, List<RequestList> inside) {
        super(outside, inside);
    }

    @Override
    public Position decideTarget(Position currentFloor, Position lastFloor,
                                 HashSet<Position> reachablePos)
            throws InterruptedException {
        if (inside().isEmpty()) {
            RequestList farest = outside().getFarestRequest(currentFloor, lastFloor,
                    reachablePos);
            if (farest.equals(PersonQueue.EXIT)) {
                return EXIT;
            } else {
                return new Position(Building.valueOf(String.valueOf(
                        farest.nowRequest().getFromBuilding()))
                        , farest.nowRequest().getFromFloor());
            }
        } else {
            Direction direction = new Direction(currentFloor, lastFloor);
            int farestFloor;
            if (direction.getVertical() > 0) {
                farestFloor = 0;
                for (RequestList r : inside()) {
                    if (r.nowRequest().getToFloor() > farestFloor) {
                        farestFloor = r.nowRequest().getToFloor();
                    }
                }
            } else {
                farestFloor = 11;
                for (RequestList r : inside()) {
                    if (r.nowRequest().getToFloor() < farestFloor) {
                        farestFloor = r.nowRequest().getToFloor();
                    }
                }
            }
            return new Position(currentFloor.getBuilding(), farestFloor);
        }
    }

    public Position decideTargetInOut(Position currentPosition, Position lastPosition,
                                      HashSet<Position> reachablePos)
            throws InterruptedException {
        if (inside().isEmpty()) {
            RequestList farest = outside().getFarestRequestInOut(currentPosition, lastPosition,
                    reachablePos);
            if (farest.equals(PersonQueueVertical.EXIT)) {
                return EXIT;
            } else {
                Building building = Building.
                        valueOf(String.valueOf(farest.nowRequest().getFromBuilding()));
                if (farest.nowRequest().getFromFloor() != currentPosition.getFloor()) {
                    return new Position(building, farest.nowRequest().getFromFloor());
                } else {
                    return new Position(building, farest.nowRequest().getToFloor());
                }
            }
        } else {
            Direction direction = new Direction(currentPosition, lastPosition);
            int farestFloor;
            if (direction.getVertical() > 0) {
                farestFloor = 0;
                for (RequestList r : inside()) {
                    if (r.nowRequest().getToFloor() > farestFloor) {
                        farestFloor = r.nowRequest().getToFloor();
                    }
                }
            } else {
                farestFloor = 11;
                for (RequestList r : inside()) {
                    if (r.nowRequest().getToFloor() < farestFloor) {
                        farestFloor = r.nowRequest().getToFloor();
                    }
                }
            }
            return new Position(currentPosition.getBuilding(), farestFloor);
        }
    }
}
