import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PersonQueueVertical extends PersonQueue {
    private final List<RequestList> personRequests;//乘侯表？
    private final Map<Integer, List<RequestList>> requestPerFloor = new HashMap<>();
    //<floor, Queue>
    private boolean end = false;

    PersonQueueVertical() {
        personRequests = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            List<RequestList> r = new ArrayList<>();
            requestPerFloor.put(i, r);
        }
    }

    public synchronized void addRequest(RequestList request) {
        personRequests.add(request);
        requestPerFloor.get(request.nowRequest().getFromFloor()).add(request);
        //System.out.println("person queue add request " + request.toString());
        notifyAll();
    }

    public synchronized void setEnd(boolean end) {
        this.end = end;
        personRequests.add(PersonQueueVertical.EXIT);
        notifyAll();
    }

    public synchronized RequestList getFarestRequest(
            Position currentFloor, Position lastFloor,
            HashSet<Position> reachablePos) throws InterruptedException {
        Direction direction = new Direction(currentFloor, lastFloor);
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

        if (direction.getVertical() > 0) {
            for (int i = 10; i >= currentFloor.getFloor(); i--) {
                Position position = new Position(currentFloor.getBuilding(),i);
                RequestList request = containSameDirection(position, direction,reachablePos);
                if (request != null) {
                    return request;
                }
            }
            for (int i = 10; i >= 1; i--) {
                if (!requestPerFloor.get(i).isEmpty()) {
                    return requestPerFloor.get(i).get(0);
                }
            }
        } else {
            for (int i = 1; i <= currentFloor.getFloor(); i++) {
                Position position = new Position(currentFloor.getBuilding(),i);
                RequestList request = containSameDirection(position, direction,reachablePos);
                if (request != null) {
                    return request;
                }
            }
            for (int i = 1; i <= 10; i++) {
                if (!requestPerFloor.get(i).isEmpty()) {
                    return requestPerFloor.get(i).get(0);
                }
            }
        }
        throw new InterruptedException();
    }

    public synchronized RequestList getFarestRequestInOut(
            Position currentPosition, Position lastPosition,
            HashSet<Position> reachablePos) throws InterruptedException {
        Direction direction = new Direction(currentPosition,
                lastPosition);
        if (this.personRequests.isEmpty() && this.end) {
            return EXIT;
        } else if (this.personRequests.isEmpty()) {
            return EXIT;
        } else if (this.personRequests.get(0).equals(EXIT)) {
            return EXIT;
        }
        if (direction.getVertical() > 0) {
            for (int i = 10; i >= currentPosition.getFloor(); i--) {
                Position position = new Position(currentPosition.getBuilding(),i);
                RequestList request = containSameDirection(position, direction,reachablePos);
                if (request != null) {
                    return request;
                }
            }
            for (int i = 10; i >= 1; i--) {
                if (!requestPerFloor.get(i).isEmpty()) {
                    return requestPerFloor.get(i).get(0);
                }
            }
        } else {
            for (int i = 1; i <= currentPosition.getFloor(); i++) {
                Position position = new Position(currentPosition.getBuilding(),i);
                RequestList request = containSameDirection(position, direction,reachablePos);
                if (request != null) {
                    return request;
                }
            }
            for (int i = 1; i <= 10; i++) {
                if (!requestPerFloor.get(i).isEmpty()) {
                    return requestPerFloor.get(i).get(0);
                }
            }
        }
        throw new InterruptedException();
    }

    public synchronized List<RequestList> getInPerson(int maxNum,
                                                        Position currentFloor,
                                                        Direction direction,
                                                      HashSet<Position> reachablePos) {
        int num = 0;
        ArrayList<RequestList> ans = new ArrayList<>();
        if (personRequests.isEmpty()) {
            notifyAll();
            return ans;
        } else if (personRequests.get(0).equals(PersonQueueVertical.EXIT)) {
            notifyAll();
            return ans;
        }
        Iterator<RequestList> it = requestPerFloor.get(currentFloor.getFloor()).iterator();
        while (it.hasNext()) {
            RequestList r = it.next();
            if (num >= maxNum) {
                break;
            }
            if ((r.nowRequest().getToFloor() - r.nowRequest().getFromFloor())
                    * direction.getVertical() >= 0) {
                num = num + 1;
                ans.add(r);
                it.remove();
                personRequests.remove(r);
            }
        }
        notifyAll();
        return ans;
    }

    public synchronized boolean isEmpty() {
        return personRequests.isEmpty();
    }

    public synchronized RequestList containSameDirection(Position currentFloor,
                                                          Direction direction,
                                                         HashSet<Position> reachablePos) {
        for (RequestList r : requestPerFloor.get(currentFloor.getFloor())) {
            int reqDirection = r.nowRequest().getToFloor() - r.nowRequest().getFromFloor();
            if (reqDirection * direction.getVertical() >= 0) {
                return r;
            }
        }
        return null;
    }
}
