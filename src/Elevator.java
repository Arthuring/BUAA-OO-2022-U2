import com.oocourse.elevator3.ElevatorRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class Elevator extends Thread {
    private abstract static class Status {
        private final boolean needSleep;

        Status(boolean needSleep) {
            this.needSleep = needSleep;
        }

        protected boolean needSleep() {
            return needSleep;
        }

        public abstract void handle(Elevator elevator, Position target)
                throws InterruptedException;
    }

    private static class Waiting extends Status {
        Waiting(boolean needSleep) {
            super(needSleep);
        }

        public void handle(Elevator elevator, Position target) throws InterruptedException {
            if (elevator.end && elevator.inside.isEmpty() && elevator.personQueue.isEmpty()) {
                throw new InterruptedException();
            }

            Position t = elevator.strategy.decideTarget(elevator.currentPosition,
                    elevator.lastPosition, elevator.reachablePostion);
            elevator.target = t;
            if (elevator.strategy instanceof LookStrategyVertical) {
                elevator.direction = new Direction(t, elevator.currentPosition);
            } else if (elevator.strategy instanceof LookStrategyHorizontal) {
                elevator.direction = new Direction(t.getFloor(), 0);
                elevator.target = new Position(t.getBuilding(),
                        elevator.currentPosition.getFloor());
            }
            if (t == Strategy.EXIT) {
                elevator.setEnd(true);
                throw new InterruptedException();

            }
            /*TODO: add a current building parameter*/
            if (elevator.personQueue.containSameDirection(elevator.
                    currentPosition, elevator.direction, elevator.reachablePostion) != null &&
                    elevator.inside.size() < elevator.capacity) {
                elevator.status = new Opening(false);
                return;
            }

            if (elevator.target.equals(elevator.currentPosition)) {
                elevator.status = new Opening(false);
            } else {
                if (needSleep()) {
                    sleep(elevator.speed);
                }
                if (elevator.strategy instanceof LookStrategyVertical) {
                    elevator.status = new RunningVertical(false);
                } else if (elevator.strategy instanceof LookStrategyHorizontal) {
                    elevator.status = new RunningHorizontal(false);
                }
            }

        }
    }

    private static class RunningVertical extends Status {
        RunningVertical(boolean needSleep) {
            super(needSleep);
        }

        public void handle(Elevator elevator, Position target) {
            int flag = 0;
            while (!elevator.currentPosition.equals(target)) {
                try {
                    if (elevator.currentPosition.getFloor() < target.getFloor()) {
                        elevator.lastPosition = elevator.currentPosition;
                        elevator.currentPosition = new Position(elevator.lastPosition.getBuilding(),
                                elevator.lastPosition.getFloor() + 1);
                    } else {
                        elevator.lastPosition = elevator.currentPosition;
                        elevator.currentPosition = new Position(elevator.lastPosition.getBuilding(),
                                elevator.lastPosition.getFloor() - 1);
                    }

                    String out = "ARRIVE-" + elevator.currentPosition.getBuilding() + "-" +
                            elevator.currentPosition.getFloor() +
                            "-" + elevator.id;
                    OutputThread.println(out);
                    System.out.println(out);
                    elevator.direction = new Direction(elevator.currentPosition,
                            elevator.lastPosition);
                    //判断一下这层有没有要下车的乘客，有的话开门
                    for (RequestList r : elevator.inside) {
                        if (r.nowRequest().getToFloor() == elevator.currentPosition.getFloor()) {
                            flag = 1;
                            break;
                        }
                    }
                    if (flag == 1) {
                        break;
                    }
                    //判断一下这层有没有同方向的乘客，有的话就直接开门
                    if (elevator.inside.size() < elevator.capacity && elevator.personQueue.
                            containSameDirection(elevator.currentPosition, elevator.direction,
                                    elevator.reachablePostion) != null) {
                        flag = 1;
                        break;
                    }
                    if (elevator.currentPosition.equals(elevator.target)) {
                        break;
                    }
                    sleep(elevator.speed);

                } catch (Exception e) {
                    //
                }
            }
            if (elevator.currentPosition.getFloor() != elevator.target.getFloor()) {
                elevator.status = new Opening(false);
            } else {
                if (elevator.inside.isEmpty()) {
                    if (flag == 1) { //有人要上下
                        elevator.status = new Opening(false);
                    } else {
                        elevator.status = new Waiting(true);
                    }
                } else {
                    elevator.status = new Opening(false);
                }
            }

        }
    }

    public static class RunningHorizontal extends Status {
        RunningHorizontal(boolean needSleep) {
            super(needSleep);
        }

        public void handle(Elevator elevator, Position target) {
            int flag = 0;
            while (!elevator.currentPosition.equals(target)) {
                try {
                    if (elevator.direction.getHorizontal() >= 0) {
                        elevator.lastPosition = elevator.currentPosition;
                        elevator.currentPosition = new Position(
                                Building.nextIncrease(elevator.lastPosition.getBuilding()),
                                elevator.lastPosition.getFloor());
                    } else {
                        elevator.lastPosition = elevator.currentPosition;
                        elevator.currentPosition = new Position(
                                Building.nextDecrease(elevator.lastPosition.getBuilding()),
                                elevator.lastPosition.getFloor());
                    }
                    String out = "ARRIVE-" + elevator.currentPosition.getBuilding() + "-" +
                            elevator.currentPosition.getFloor() +
                            "-" + elevator.id;
                    OutputThread.println(out);
                    System.out.println(out);
                    elevator.direction = new Direction(elevator.currentPosition,
                            elevator.lastPosition);
                    if (elevator.reachablePostion.contains(elevator.currentPosition)) {
                        for (RequestList r : elevator.inside) {
                            if (elevator.currentPosition.getBuilding().
                                    equals(Building.toBuilding(r.nowRequest().getToBuilding()))) {
                                flag = 1;
                                break;
                            }
                        }
                        if (flag == 1) {
                            break;
                        } //判断一下这层有没有可以上车乘客，有的话就直接开门
                        if (elevator.inside.size() < elevator.capacity && elevator.personQueue.
                                containSameDirection(elevator.currentPosition, elevator.direction,
                                        elevator.reachablePostion) != null) {
                            flag = 1;
                            break;
                        }
                        if (elevator.currentPosition.equals(elevator.target)) {
                            break;
                        }
                    }
                    sleep(elevator.speed);
                } catch (Exception e) { //
                }
            }
            if (!elevator.currentPosition.getBuilding().equals(elevator.target.getBuilding())) {
                elevator.status = new Opening(false);
            } else {
                if (elevator.inside.isEmpty()) {
                    if (flag == 1) { //有人要上下
                        elevator.status = new Opening(false);
                    } else {
                        elevator.status = new Waiting(true);
                    }
                } else {
                    elevator.status = new Opening(false);
                }
            }
        }
    }

    private static class Opening extends Status {
        Opening(boolean needSleep) {
            super(needSleep);
        }

        public void handle(Elevator elevator, Position target) {
            try {
                String out = "OPEN-" + elevator.currentPosition.getBuilding() +
                        "-" + elevator.currentPosition.getFloor() + "-" + elevator.id;
                //elevator.outPutStream.putString(out);
                OutputThread.println(out);
                System.out.println(out);
                elevator.passengerOut(elevator);
                sleep(400);
                ///////////////////////////////////////////////
                if (elevator.strategy instanceof LookStrategyVertical) {
                    Position t = elevator.strategy.decideTargetInOut(
                            elevator.currentPosition, elevator.lastPosition,
                            elevator.reachablePostion);
                    if ((!t.equals(Strategy.EXIT)) && !t.equals(elevator.target)) {
                        elevator.direction = new Direction(t, elevator.currentPosition);
                    }
                }
                //////////////////////////////////////////////
                elevator.passengerIn(elevator);
                String close = "CLOSE-" + elevator.currentPosition.getBuilding() +
                        "-" + elevator.currentPosition.getFloor() + "-" + elevator.id;
                //elevator.outPutStream.putString(close);
                OutputThread.println(close);
                System.out.println(close);
                if (elevator.strategy instanceof LookStrategyVertical) {
                    sleep(elevator.speed);
                } else if (elevator.strategy instanceof LookStrategyHorizontal) {
                    sleep(elevator.speed);
                }
                elevator.status = new Waiting(false);
            } catch (Exception e) {
                //
            }
        }
    }

    private Building building;
    private final int floor;
    private final int id;
    private final PersonQueue personQueue;  //outside Queue
    private final Strategy strategy; //define target
    private final List<RequestList> inside = new ArrayList<>(); // inside Queue
    private Status status = new Waiting(false);
    private Position currentPosition;
    private Position target;
    private boolean end = false;
    private final int capacity;
    private Direction direction;
    private Position lastPosition = new Position(Building.B, 0);
    private final int speed;
    private final HashSet<Position> reachablePostion = new HashSet<>();
    private final Type type;

    public enum Type {
        building, floor
    }

    public Elevator(int id, char building, int floor,
                    PersonQueue personQueue, int capacity, String type) {
        this.building = Building.toBuilding(building);
        this.id = id;
        //this.dispatcher = dispatcher;
        this.currentPosition = new Position(Building.toBuilding(building), floor);
        this.personQueue = personQueue;
        if (type.equals("building")) {
            this.strategy = new LookStrategyVertical(personQueue, inside);
            for (int i = 1; i < 11; i++) {
                Position position = new Position(Building.toBuilding(building), i);
                this.reachablePostion.add(position);
            }
            this.type = Type.valueOf("building");
            this.floor = -1;
        } else if (type.equals("floor")) {
            this.strategy = new LookStrategyHorizontal(personQueue, inside);
            for (int i = 0; i < 5; i++) {
                Position position = new Position(Building.toBuilding((char) ('A' + i)), 1);
                this.reachablePostion.add(position);
            }
            this.type = Type.valueOf("floor");
            this.floor = floor;
        } else {
            this.strategy = null;
            this.type = null;
            this.floor = -1;
        }
        this.speed = 600;
        this.capacity = 8;

    }

    public Elevator(int id, char building, int floor,
                    PersonQueue personQueue, ElevatorRequest request) {
        this.id = id;
        //this.dispatcher = dispatcher;
        this.currentPosition = new Position(Building.toBuilding(building), floor);
        this.personQueue = personQueue;
        String type = request.getType();
        if (type.equals("building")) {
            this.strategy = new LookStrategyVertical(personQueue, inside);
            for (int i = 1; i < 11; i++) {
                Position position = new Position(Building.toBuilding(building), i);
                this.reachablePostion.add(position);
            }
            this.type = Type.valueOf("building");
            this.floor = -1;
        } else if (type.equals("floor")) {
            this.strategy = new LookStrategyHorizontal(personQueue, inside);
            for (int i = 0; i < 5; i++) {
                int m = request.getSwitchInfo();
                if (((m >> (i)) & 1) == 1) {
                    Position position = new Position(Building.toBuilding((char) ('A' + i)), floor);
                    this.reachablePostion.add(position);
                }
            }
            this.type = Type.valueOf("floor");
            this.floor = request.getFloor();
        } else {
            this.strategy = null;
            this.type = null;
            this.floor = -1;
        }
        this.capacity = request.getCapacity();
        this.speed = (int) (request.getSpeed() * 1000);
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public Type getType() {
        return this.type;
    }

    public Position getCurrentPosition() {
        return this.currentPosition;
    }

    public HashSet<Position> getReachablePostion() {
        return this.reachablePostion;
    }

    @Override
    public void run() {
        try {
            sleep(this.speed);
        } catch (Exception e) {
            //
        }
        while (true) {
            if (this.end && personQueue.isEmpty() && inside.isEmpty()) {
                // System.out.println("Elevator" + this.id + " End");
                //outPutStream.setEnd();
                break;
            } else {
                try {
                    status.handle(this, this.target);
                } catch (InterruptedException e) {
                    System.out.println("Elevator" + this.id + " End");
                    //outPutStream.setEnd();
                    break;
                }
            }
        }
    }

    public void passengerOut(Elevator elevator) throws InterruptedException {
        Iterator<RequestList> it = elevator.inside.iterator();
        while (it.hasNext()) {
            RequestList r = it.next();
            if (r.nowRequest().getToFloor() == elevator.currentPosition.getFloor() &&
                    elevator.currentPosition.getBuilding().
                            equals(Building.valueOf(String.valueOf(r.nowRequest()
                                    .getToBuilding())))) {

                String out = "OUT-" + r.nowRequest().getPersonId() + "-" +
                        elevator.currentPosition.getBuilding() +
                        "-" + elevator.currentPosition.getFloor() + "-" + elevator.id + "";
                //this.outPutStream.putString(out);
                OutputThread.println(out);
                System.out.println(out);
                it.remove();
                if (r.hasNext()) {
                    Dispatcher.getInstance().addPersonRequest(r);
                } else {
                    RequestCounter.getInstance().release();
                }
            }
        }
    }

    public void passengerIn(Elevator elevator) throws InterruptedException {
        /*TODO:*/
        int maxNum = elevator.capacity - elevator.inside.size();
        List<RequestList> addList = elevator.personQueue.getInPerson(maxNum,
                elevator.currentPosition, elevator.direction, this.reachablePostion);
        for (RequestList r : addList) {
            String out = "IN-" + r.nowRequest().getPersonId() + "-" +
                    elevator.currentPosition.getBuilding() +
                    "-" + elevator.currentPosition.getFloor() + "-" + elevator.id + "";
            //elevator.outPutStream.putString(out);
            OutputThread.println(out);
            System.out.println(out);
            inside.add(r);
        }
    }
}

