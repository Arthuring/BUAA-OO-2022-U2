import com.oocourse.elevator3.PersonRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PersonQueueHorizontal extends PersonQueue {
    private final List<PersonRequest> personRequests;//乘侯表？
    private final Map<Building, List<PersonRequest>> requestPerBuilding = new HashMap<>();
    //<floor, Queue>
    private boolean end = false;

    PersonQueueHorizontal() {
        personRequests = new ArrayList<>();
        for (char i = 'A'; i < 'F'; i++) {
            List<PersonRequest> r = new ArrayList<>();
            requestPerBuilding.put(Building.valueOf(String.valueOf(i)), r);
        }
    }

    public synchronized void addRequest(PersonRequest request) {
        personRequests.add(request);
        requestPerBuilding.get(Building.toBuilding(request.getFromBuilding())).add(request);
        notifyAll();
    }

    public synchronized void setEnd(boolean end) {
        this.end = end;
        personRequests.add(PersonQueue.EXIT);
        notifyAll();
    }

    public synchronized PersonRequest getFarestRequest(
            Position currentPosition, Position lastPosition) throws InterruptedException {
        Direction direction = new Direction(currentPosition, lastPosition);
        while (this.personRequests.isEmpty()) {
            if (this.end) {
                return EXIT;
            } else {
                wait();
            }
        }
        if (this.personRequests.get(0).equals(EXIT)) {
            return EXIT;
        }

        Building building = currentPosition.getBuilding();
        do {
            if (!requestPerBuilding.get(building).isEmpty()) {
                return requestPerBuilding.get(building).get(0);
            }
            if (direction.getHorizontal() >= 0) {
                building = Building.nextIncrease(building);
            } else {
                building = Building.nextDecrease(building);
            }
        } while (!building.equals(currentPosition.getBuilding()));
        throw new InterruptedException("NOOO REQUEST ??");
    }

    public synchronized PersonRequest getFarestRequestInOut(
            Position currentPosition, Position lastPosition) throws InterruptedException {
        Direction direction = new Direction(currentPosition, lastPosition);
        if (this.personRequests.isEmpty() && this.end) {
            return EXIT;
        } else if (this.personRequests.isEmpty()) {
            return EXIT;
        } else if (this.personRequests.get(0).equals(EXIT)) {
            return EXIT;
        }

        Building building = currentPosition.getBuilding();
        do {
            if (!requestPerBuilding.get(building).isEmpty()) {
                return requestPerBuilding.get(building).get(0);
            }
            if (direction.getHorizontal() >= 0) {
                building = Building.nextIncrease(currentPosition.getBuilding());
            } else {
                building = Building.nextDecrease(currentPosition.getBuilding());
            }
        } while (!building.equals(currentPosition.getBuilding()));
        throw new InterruptedException("NOOO REQUEST ??");
    }

    public synchronized List<PersonRequest> getInPerson(int maxNum,
                                                        Position currentPosition,
                                                        Direction direction) {
        int num = 0;
        ArrayList<PersonRequest> ans = new ArrayList<>();
        if (personRequests.isEmpty()) {
            /*TODO:need notifiy all ?*/
            return ans;
        }
        Iterator<PersonRequest> it = requestPerBuilding.
                get(currentPosition.getBuilding()).iterator();
        while (it.hasNext()) {
            PersonRequest r = it.next();
            if (num >= maxNum) {
                break;
            }
            num = num + 1;
            ans.add(r);
            it.remove();
            personRequests.remove(r);
        }
        /*TODO:need notify all ????*/
        return ans;
    }

    public synchronized boolean isEmpty() {
        return personRequests.isEmpty();
    }

    public synchronized PersonRequest containSameDirection(Position currentFloor,
                                                           Direction direction) {
        for (PersonRequest r : requestPerBuilding.get(currentFloor.getBuilding())) {
            return r;
        }
        return null;
    }
}
