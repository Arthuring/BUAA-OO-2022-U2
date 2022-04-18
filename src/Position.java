import java.util.Objects;

public class Position {
    private final Building building;
    private final int floor;

    Position(Building building, int floor) {
        this.building = building;
        this.floor = floor;
    }

    public Building getBuilding() {
        return building;
    }

    public int getFloor() {
        return floor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Position position = (Position) o;
        return building == position.building && floor == position.floor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(building, floor);
    }

    @Override
    public String toString() {
        return "Position{" +
                "building=" + building +
                ", floor=" + floor +
                '}';
    }
}
