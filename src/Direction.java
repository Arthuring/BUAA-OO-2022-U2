public class Direction {
    private final int horizontal;
    private final int vertical;

    public Direction(Position current, Position last) {
        vertical = current.getFloor() - last.getFloor();
        horizontal = Building.getDirection(current.getBuilding(), last.getBuilding());
    }

    public Direction(int horizontal, int vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public int getVertical() {
        return vertical;
    }

    public int getHorizontal() {
        return horizontal;
    }

    public String toString() {
        return "Horizontal(" + horizontal + ") Vertical(" + vertical + ")";
    }
}
