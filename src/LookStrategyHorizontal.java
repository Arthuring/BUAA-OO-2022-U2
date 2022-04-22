import java.util.HashSet;
import java.util.List;

public class LookStrategyHorizontal extends Strategy {
    protected LookStrategyHorizontal(PersonQueue outside, List<RequestList> inside) {
        super(outside, inside);
    }

    @Override
    public Position decideTarget(Position currentPosition,
                                 Position lastPosition,
                                 HashSet<Position> reachablePos) throws InterruptedException {
        if (inside().isEmpty()) {
            RequestList farest = outside().getFarestRequest(currentPosition, lastPosition,
                    reachablePos);
            if (farest.equals(PersonQueue.EXIT)) {
                return EXIT;
            } else {
                Building building = Building.toBuilding(farest.nowRequest().getFromBuilding());
                int distIncrease = Building.getIncreaseDistantA2B(
                        currentPosition.getBuilding(), building);
                int distDecrease = Building.getDecreaseDistantA2B(
                        currentPosition.getBuilding(), building);
                if (distIncrease <= distDecrease) {
                    return new Position(building, 11);
                } else {
                    return new Position(building, -11);
                }
            }
        } else {
            Direction direction = getDirectionInsideShortest();
            Building building = getTargetInsideLook(direction,currentPosition,reachablePos);
            return new Position(building, direction.getHorizontal());
        }
    }

    public Building getTargetInsideLook(Direction direction,Position current,
                                        HashSet<Position> reachablePos) {
        Building building = current.getBuilding();
        if (direction.getHorizontal() >= 0) {
            int dist = 0;
            for (RequestList r : inside()) {
                if (Building.getIncreaseDistantA2B(current.getBuilding(),
                        Building.toBuilding(r.nowRequest().getToBuilding())) >= dist) {
                    building = Building.toBuilding(r.nowRequest().getToBuilding());
                    dist = Building.getIncreaseDistantA2B(current.getBuilding(),
                            Building.toBuilding(r.nowRequest().getToBuilding()));
                }
            }
        } else {
            int dist = 0;
            for (RequestList r : inside()) {
                if (Building.getDecreaseDistantA2B(current.getBuilding(),
                        Building.toBuilding(r.nowRequest().getToBuilding())) >= dist) {
                    building = Building.toBuilding(r.nowRequest().getToBuilding());
                    dist = Building.getIncreaseDistantA2B(current.getBuilding(),
                            Building.toBuilding(r.nowRequest().getToBuilding()));
                }
            }
        }
        return building;
    }

    public Direction getDirectionInsideShortest() {
        int distIncrease = 0;
        int distDecrease = 0;
        for (RequestList r : inside()) {
            Building from = Building.toBuilding(r.nowRequest().getFromBuilding());
            Building to = Building.toBuilding(r.nowRequest().getToBuilding());
            distIncrease = distIncrease + Building.getIncreaseDistantA2B(from, to);
            distDecrease = distDecrease + Building.getDecreaseDistantA2B(from, to);
        }
        if (distIncrease <= distDecrease) {
            return new Direction(11, 0);
        } else {
            return new Direction(-11, 0);
        }
    }

    @Override
    /*TODO: this is wrong function, please change when consider diretion*/
    public Position decideTargetInOut(Position currentPosition, Position lastPosition,
                                      HashSet<Position> reachablePos)
            throws InterruptedException {
        if (inside().isEmpty()) {
            RequestList farest = outside().getFarestRequestInOut(currentPosition, lastPosition,
                    reachablePos);
            if (farest.equals(PersonQueue.EXIT)) {
                return EXIT;
            } else {
                return new Position(Building.toBuilding(farest.nowRequest().getFromBuilding()),
                        farest.nowRequest().getFromFloor());
            }
        } else {
            Direction direction = new Direction(currentPosition, lastPosition);
            Building building;
            if (direction.getHorizontal() >= 0) {
                building = Building.A;
                for (RequestList r : inside()) {
                    if (Building.toBuilding(r.nowRequest().getToBuilding()).ordinal() >
                            building.ordinal()) {
                        building = Building.toBuilding(r.nowRequest().getToBuilding());
                    }
                }
            } else {
                building = Building.E;
                for (RequestList r : inside()) {
                    if (Building.toBuilding(r.nowRequest().getToBuilding()).ordinal()
                            < building.ordinal()) {
                        building = Building.toBuilding(r.nowRequest().getToBuilding());
                    }
                }
            }
            return new Position(building, currentPosition.getFloor());
        }
    }
}
