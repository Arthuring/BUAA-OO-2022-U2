
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PersonQueueHorizontal extends PersonQueue {
    private final List<RequestList> personRequests;//乘侯表？
    private final Map<Building, List<RequestList>> requestPerBuilding = new HashMap<>();
    private boolean end = false;

    PersonQueueHorizontal() {
        personRequests = new ArrayList<>();
        for (char i = 'A'; i < 'F'; i++) {
            List<RequestList> r = new ArrayList<>();
            requestPerBuilding.put(Building.valueOf(String.valueOf(i)), r);
        }
    }

    public synchronized void addRequest(RequestList request) {
        personRequests.add(request);
        requestPerBuilding.get(Building.toBuilding(request.nowRequest().getFromBuilding()))
                .add(request);
        notifyAll();
    }

    public synchronized void setEnd(boolean end) {
        this.end = end;
        personRequests.add(PersonQueue.EXIT);
        notifyAll();
    }

    public synchronized boolean containReachable(HashSet<Position> reachable) {
        if (personRequests.isEmpty()) {
            return false;
        }
        if (personRequests.get(0).equals(PersonQueue.EXIT)) {
            return false;
        }
        for (RequestList r : personRequests) {
            Position from = new Position(Building.toBuilding(r.
                    nowRequest().getFromBuilding()),
                    r.nowRequest().getFromFloor());
            Position to = new Position(Building.toBuilding(r.
                    nowRequest().getToBuilding()),
                    r.nowRequest().getToFloor());
            if (reachable.contains(from) && reachable.contains(to)) {
                return true;
            }
        }
        return false;
    }

    public synchronized RequestList getFarestRequest(
            Position currentPosition, Position lastPosition,
            HashSet<Position> reachablePos) throws InterruptedException {
        Direction direction = new Direction(currentPosition, lastPosition);
        while (this.personRequests.isEmpty() || !containReachable(reachablePos)) {
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
                for (RequestList r : requestPerBuilding.get(building)) {
                    Position from = new Position(Building.toBuilding(r.
                            nowRequest().getFromBuilding()),
                            r.nowRequest().getFromFloor());
                    Position to = new Position(Building.toBuilding(r.
                            nowRequest().getToBuilding()),
                            r.nowRequest().getToFloor());
                    if (reachablePos.contains(from) && reachablePos.contains(to)) {
                        return r;
                    }
                }
            }
            if (direction.getHorizontal() >= 0) {
                building = Building.nextIncrease(building);
            } else {
                building = Building.nextDecrease(building);
            }
        } while (!building.equals(currentPosition.getBuilding()));
        throw new InterruptedException("NOOO REQUEST ??");
    }

    public synchronized RequestList getFarestRequestInOut(
            Position currentPosition, Position lastPosition,
            HashSet<Position> reachablePos) throws InterruptedException {
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

    public synchronized List<RequestList> getInPerson(int maxNum,
                                                      Position currentPosition,
                                                      Direction direction,
                                                      HashSet<Position> reachablePos) {
        int num = 0;
        ArrayList<RequestList> ans = new ArrayList<>();
        if (personRequests.isEmpty()) {
            /*TODO:need notifiy all ?*/
            return ans;
        }
        Iterator<RequestList> it = requestPerBuilding.
                get(currentPosition.getBuilding()).iterator();
        while (it.hasNext()) {
            RequestList r = it.next();
            if (num >= maxNum) {
                break;
            }
            Position toPosition = new Position(Building.toBuilding(r.nowRequest().getToBuilding())
                    , currentPosition.getFloor());
            if (reachablePos.contains(toPosition)) {
                num = num + 1;
                ans.add(r);
                it.remove();
                personRequests.remove(r);
            }
        }
        /*TODO:need notify all ????*/
        return ans;
    }

    public synchronized boolean isEmpty() {
        return personRequests.isEmpty();
    }

    public synchronized RequestList containSameDirection(Position currentPosition,
                                                         Direction direction,
                                                         HashSet<Position> reachablePos) {
        if (reachablePos.contains(currentPosition)) {
            for (RequestList r : requestPerBuilding.get(currentPosition.getBuilding())) {
                Position toPosition = new Position(Building.toBuilding(r.nowRequest()
                        .getToBuilding()), currentPosition.getFloor());
                if (reachablePos.contains(toPosition)) {
                    return r;
                }
            }
        }
        return null;
    }
}
