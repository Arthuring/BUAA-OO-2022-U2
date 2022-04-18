import com.oocourse.elevator3.PersonRequest;

import java.util.List;

public class LookStrategyHorizontal extends Strategy {
    protected LookStrategyHorizontal(PersonQueue outside, List<PersonRequest> inside) {
        super(outside, inside);
        super.setspeed(200);
    }

    @Override
    public Position decideTarget(Position currentPosition,
                                 Position lastPosition) throws InterruptedException {
        if (inside().isEmpty()) {
            Direction direction = new Direction(currentPosition, lastPosition);
            PersonRequest farest = outside().getFarestRequest(currentPosition, lastPosition);
            if (farest.equals(PersonQueue.EXIT)) {
                return EXIT;
            } else {
                Building building = Building.toBuilding(farest.getFromBuilding());
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
            Building building = getTargetInsideLook(direction,currentPosition);
            return new Position(building, direction.getHorizontal());
        }
    }

    public Building getTargetInsideLook(Direction direction,Position current) {
        Building building = current.getBuilding();
        if (direction.getHorizontal() >= 0) {
            int dist = 0;
            for (PersonRequest r : inside()) {
                if (Building.getIncreaseDistantA2B(current.getBuilding(),
                        Building.toBuilding(r.getToBuilding())) >= dist) {
                    building = Building.toBuilding(r.getToBuilding());
                    dist = Building.getIncreaseDistantA2B(current.getBuilding(),
                            Building.toBuilding(r.getToBuilding()));
                }
            }
        } else {
            int dist = 0;
            for (PersonRequest r : inside()) {
                if (Building.getDecreaseDistantA2B(current.getBuilding(),
                        Building.toBuilding(r.getToBuilding())) >= dist) {
                    building = Building.toBuilding(r.getToBuilding());
                    dist = Building.getIncreaseDistantA2B(current.getBuilding(),
                            Building.toBuilding(r.getToBuilding()));
                }
            }
        }
        return building;
    }

    public Direction getDirectionInsideShortest() {
        int distIncrease = 0;
        int distDecrease = 0;
        for (PersonRequest r : inside()) {
            Building from = Building.toBuilding(r.getFromBuilding());
            Building to = Building.toBuilding(r.getToBuilding());
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
    public Position decideTargetInOut(Position currentPosition, Position lastPosition)
            throws InterruptedException {
        if (inside().isEmpty()) {
            PersonRequest farest = outside().getFarestRequestInOut(currentPosition, lastPosition);
            if (farest.equals(PersonQueue.EXIT)) {
                return EXIT;
            } else {
                return new Position(Building.toBuilding(farest.getFromBuilding()),
                        farest.getFromFloor());
            }
        } else {
            Direction direction = new Direction(currentPosition, lastPosition);
            Building building;
            if (direction.getHorizontal() >= 0) {
                building = Building.A;
                for (PersonRequest r : inside()) {
                    if (Building.toBuilding(r.getToBuilding()).ordinal() > building.ordinal()) {
                        building = Building.toBuilding(r.getToBuilding());
                    }
                }
            } else {
                building = Building.E;
                for (PersonRequest r : inside()) {
                    if (Building.toBuilding(r.getToBuilding()).ordinal() < building.ordinal()) {
                        building = Building.toBuilding(r.getToBuilding());
                    }
                }
            }
            return new Position(building, currentPosition.getFloor());
        }
    }
}
