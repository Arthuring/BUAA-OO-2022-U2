import com.oocourse.elevator3.ElevatorRequest;
import com.oocourse.elevator3.PersonRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dispatcher {
    private final Map<String, PersonQueue> personQueueListBuilding = new HashMap<>();//乘侯表
    private final Map<Integer, PersonQueue> personQueueListFloor = new HashMap<>();
    private List<Elevator> elevators = new ArrayList<>();
    //private Set<PersonRequest> requestSet = new HashSet<>();//来的请求
    private boolean end = false;

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
    }

    public synchronized void addPersonRequest(PersonRequest request) {
        if (request.getFromBuilding() == request.getToBuilding()) {
            personQueueListBuilding.get(String.valueOf(request.getFromBuilding()))
                    .addRequest(request);
        } else if (request.getFromFloor() == request.getToFloor()) {
            personQueueListFloor.get(request.getFromFloor())
                    .addRequest(request);
        }

        // requestSet.add(request);
    }

    public synchronized void addElevator(ElevatorRequest request) {
        String type = request.getType();
        if (type.equals("building")) {
            Elevator elevator = new Elevator(request.getElevatorId(), request.getBuilding(), 1,
                    this.getPersonQueueListBuilding().get(String.valueOf(request.getBuilding())),
                    6, "building");
            elevators.add(elevator);
            elevator.start();
        } else if (type.equals("floor")) {
            if (!personQueueListFloor.containsKey(request.getFloor())) {
                personQueueListFloor.put(request.getFloor(), new PersonQueueHorizontal());
            }
            Elevator elevator = new Elevator(request.getElevatorId(), 'A', request.getFloor(),
                    personQueueListFloor
                            .get(request.getFloor()), 6, "floor");
            elevators.add(elevator);
            elevator.start();
        }
    }

    public void distribute() {

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
        this.elevators = elevators;
    }

    public synchronized Map<String, PersonQueue> getPersonQueueListBuilding() {
        return this.personQueueListBuilding;
    }
}
