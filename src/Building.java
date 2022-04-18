public enum Building {
    A, B, C, D, E, Z;

    public static Building nextIncrease(Building b) {
        switch (b) {
            case A:
            case B:
            case C:
            case D:
                return Building.valueOf(String.valueOf((char) (b.ordinal() + 'A' + 1)));
            case E:
                return Building.A;
            default:
                return Building.Z;
        }
    }

    public static Building nextDecrease(Building b) {
        switch (b) {
            case E:
            case B:
            case C:
            case D:
                return Building.valueOf(String.valueOf((char) (b.ordinal() + 'A' - 1)));
            case A:
                return Building.E;
            default:
                return Building.Z;
        }
    }

    public static int getIncreaseDistantA2B(Building a, Building b) {
        if (a.equals(b)) {
            return 0;
        } else {
            int distant = 0;
            Building temp = a;
            while (!temp.equals(b)) {
                temp = nextIncrease(temp);
                distant += 1;
            }
            return distant;
        }
    }

    public static int getDecreaseDistantA2B(Building a, Building b) {
        if (a.equals(b)) {
            return 0;
        } else {
            int distant = 0;
            Building temp = a;
            while (!temp.equals(b)) {
                temp = nextDecrease(temp);
                distant += 1;
            }
            return distant;
        }
    }

    public static int nearestDirection(Building a, Building b) {
        if (getIncreaseDistantA2B(a, b) >= getDecreaseDistantA2B(a, b)) {
            return 1;
        } else {
            return -1;
        }
    }

    public static int getDirection(Building currentBuilding, Building lastBuilding) {
        int direction;
        direction = currentBuilding.ordinal() - lastBuilding.ordinal();
        if (currentBuilding.equals(Building.A) && lastBuilding.equals(Building.E)) {
            return 1;
        } else if (currentBuilding.equals(Building.E) && lastBuilding.equals(Building.A)) {
            return -1;
        }
        return direction;
    }

    public static Building toBuilding(char a) {
        return Building.valueOf(String.valueOf(a));
    }
}
