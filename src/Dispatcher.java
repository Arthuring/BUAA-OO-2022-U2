import com.oocourse.elevator3.ElevatorRequest;
import com.oocourse.elevator3.PersonRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/*TODO: Make this class a single example mode!*/
public class Dispatcher {
    private static final Dispatcher DISPATCHER = new Dispatcher();
    private final Map<String, PersonQueue> personQueueListBuilding = new HashMap<>();//乘侯表
    private final Map<Integer, PersonQueue> personQueueListFloor = new HashMap<>();
    private List<Elevator> elevators = new ArrayList<>();
    private final HashMap<Integer, ArrayList<Elevator>> horiElevatorFloor = new HashMap<>();
    //private Set<PersonRequest> requestSet = new HashSet<>();//来的请求
    private boolean end = false;

    public static Dispatcher getInstance() {
        return DISPATCHER;
    }

    public Dispatcher() {
        for (int i = 0; i < 5; i++) {
            PersonQueueVertical personQueueVertical = new PersonQueueVertical();
            char a = (char) ('A' + i);
            personQueueListBuilding.put(String.valueOf(a), personQueueVertical);
        }
        for (int i = 1; i < 11; i++) {
            PersonQueueHorizontal personQueueHorizontal = new PersonQueueHorizontal();
            personQueueListFloor.put(i, personQueueHorizontal);
        }
        for (int i = 1; i < 11; i++) {
            ArrayList<Elevator> arrayList = new ArrayList<>();
            horiElevatorFloor.put(i, arrayList);
        }
    }

    public boolean canReachInFloor(Integer floor, Building fromBuilding, Building toBuilding) {
        Position from = new Position(fromBuilding, floor);
        Position to = new Position(toBuilding, floor);
        for (Elevator elevator : horiElevatorFloor.get(floor)) {
            HashSet<Position> reachable = elevator.getReachablePostion();
            if (reachable.contains(from) && reachable.contains(to)) {
                return true;
            }
        }
        return false;
    }

    public void addRawRequest(PersonRequest request) {
        Building formBuilding = Building.toBuilding(request.getFromBuilding());
        Building toBuilding = Building.toBuilding(request.getToBuilding());
        int fromFloor = request.getFromFloor();
        int toFloor = request.getToFloor();
        int midFloor = 0;
        ArrayList<PersonRequest> requests = new ArrayList<>();
        if (formBuilding.equals(toBuilding)) {
            requests.add(request);
            RequestList requestList = new RequestList(requests);
            addPersonRequest(requestList);
        } else {
            int distant = 22;
            for (int i = 1; i < 11; i++) {
                if (canReachInFloor(i, formBuilding, toBuilding)) {
                    int temp = Math.abs(fromFloor - i) + Math.abs(toFloor - i);
                    if (temp < distant) {
                        distant = temp;
                        midFloor = i;
                    }
                }
            }
            if (fromFloor != midFloor) {
                PersonRequest request1 = new PersonRequest(fromFloor, midFloor,
                        request.getFromBuilding(), request.getFromBuilding(),
                        request.getPersonId());
                requests.add(request1);
            }
            PersonRequest request2 = new PersonRequest(midFloor, midFloor,
                    request.getFromBuilding(), request.getToBuilding(), request.getPersonId());
            requests.add(request2);
            if (toFloor != midFloor) {
                PersonRequest request3 = new PersonRequest(midFloor, toFloor,
                        request.getToBuilding(), request.getToBuilding(), request.getPersonId());
                requests.add(request3);
            }
            RequestList requestList = new RequestList(requests);
            addPersonRequest(requestList);
        }
    }

    public void addPersonRequest(RequestList req) {
        if (req.hasNext()) {
            req.goToNext();
        }
        if (req.nowRequest().getFromBuilding() == req.nowRequest().getToBuilding()) {
            personQueueListBuilding.get(String.valueOf(req.nowRequest().getFromBuilding()))
                    .addRequest(req);
        } else if (req.nowRequest().getFromFloor() == req.nowRequest().getToFloor()) {
            personQueueListFloor.get(req.nowRequest().getFromFloor())
                    .addRequest(req);
        }
        // requestSet.add(req);
    }

    public synchronized void addElevator(ElevatorRequest request) {
        String type = request.getType();
        if (type.equals("building")) {
            Elevator elevator = new Elevator(request.getElevatorId(), request.getBuilding(), 1,
                    this.getPersonQueueListBuilding().get(String.valueOf(request.getBuilding())),
                    request);
            elevators.add(elevator);
            elevator.start();
        } else if (type.equals("floor")) {
            if (!personQueueListFloor.containsKey(request.getFloor())) {
                personQueueListFloor.put(request.getFloor(), new PersonQueueHorizontal());
            }
            Elevator elevator = new Elevator(request.getElevatorId(), 'A', request.getFloor(),
                    personQueueListFloor
                            .get(request.getFloor()), request);
            elevators.add(elevator);
            horiElevatorFloor.get(elevator.getCurrentPosition().getFloor()).add(elevator);
            elevator.start();
        }
    }

    public synchronized void setEnd(boolean end) {
        this.end = end;
        // System.out.println("Dispatcher end: no more request");
        for (Map.Entry<String, PersonQueue> item : personQueueListBuilding.entrySet()) {
            synchronized (item.getValue()) {
                item.getValue().setEnd(true);
            }
        }
        for (Map.Entry<Integer, PersonQueue> item : personQueueListFloor.entrySet()) {
            synchronized (item.getValue()) {
                item.getValue().setEnd(true);
            }
        }
    }

    public void setElevators(ArrayList<Elevator> elevators) {
        this.elevators.addAll(elevators);
        for (Elevator elevator : elevators) {
            if (elevator.getType().equals(Elevator.Type.floor)) {
                horiElevatorFloor.get(elevator.getCurrentPosition().getFloor()).add(elevator);
            }
        }
    }

    public Map<String, PersonQueue> getPersonQueueListBuilding() {
        return this.personQueueListBuilding;
    }

    public Map<Integer, PersonQueue> getPersonQueueListFloor() {
        return this.personQueueListFloor;
    }
}
