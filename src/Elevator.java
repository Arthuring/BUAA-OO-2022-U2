import com.oocourse.elevator3.PersonRequest;

import java.util.ArrayList;
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
                    elevator.lastPosition);
            elevator.target = t;
            if (elevator.strategy instanceof LookStrategyVertical) {
                elevator.direction = new Direction(t, elevator.currentPosition);
            } else if (elevator.strategy instanceof LookStrategyHorizontal) {
                elevator.direction = new Direction(t.getFloor(), 0);
                elevator.target = new Position(t.getBuilding(),elevator.currentPosition.getFloor());
            }
            if (t == Strategy.EXIT) {
                elevator.setEnd(true);
                throw new InterruptedException();

            }
            /*TODO: add a current building parameter*/
            if (elevator.personQueue.containSameDirection(elevator.
                    currentPosition, elevator.direction) != null &&
                    elevator.inside.size() < elevator.capacity) {
                elevator.status = new Opening(false);
                return;
            }

            if (elevator.target.equals(elevator.currentPosition)) {
                elevator.status = new Opening(false);
            } else {
                if (needSleep()) {
                    sleep(elevator.strategy.getSpeed());
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
                    elevator.direction = new Direction(elevator.currentPosition,
                            elevator.lastPosition);
                    //判断一下这层有没有要下车的乘客，有的话开门
                    for (PersonRequest r : elevator.inside) {
                        if (r.getToFloor() == elevator.currentPosition.getFloor()) {
                            flag = 1;
                            break;
                        }
                    }
                    if (flag == 1) {
                        break;
                    }
                    //判断一下这层有没有同方向的乘客，有的话就直接开门
                    if (elevator.personQueue.containSameDirection(elevator.
                            currentPosition, elevator.direction) != null &&
                            elevator.inside.size() < elevator.capacity) {
                        flag = 1;
                        break;
                    }
                    if (elevator.currentPosition.equals(elevator.target)) {
                        break;
                    }
                    sleep(400);

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
                    elevator.direction = new Direction(elevator.currentPosition,
                            elevator.lastPosition);
                    //判断一下这层有没有要下车的乘客，有的话开门
                    for (PersonRequest r : elevator.inside) {
                        if (elevator.currentPosition.getBuilding().
                                equals(Building.toBuilding(r.getToBuilding()))) {
                            flag = 1;
                            break;
                        }
                    }
                    if (flag == 1) {
                        break;
                    }
                    //判断一下这层有没有同方向的乘客，有的话就直接开门
                    if (elevator.personQueue.containSameDirection(elevator.
                            currentPosition, elevator.direction) != null &&
                            elevator.inside.size() < elevator.capacity) {
                        flag = 1;
                        break;
                    }
                    if (elevator.currentPosition.equals(elevator.target)) {
                        break;
                    }
                    sleep(200);

                } catch (Exception e) {
                    //
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
                sleep(400);
                elevator.passengerOut(elevator);
                ///////////////////////////////////////////////
                if (elevator.strategy instanceof LookStrategyVertical) {
                    Position t = elevator.strategy.decideTargetInOut(
                            elevator.currentPosition, elevator.lastPosition);
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
                if (elevator.strategy instanceof LookStrategyVertical) {
                    sleep(400);
                } else if (elevator.strategy instanceof LookStrategyHorizontal) {
                    sleep(200);
                }
                elevator.status = new Waiting(false);
            } catch (Exception e) {
                //
            }
        }
    }

    //private final Dispatcher dispatcher;
    private Building building;
    private final int id;
    private final PersonQueue personQueue;  //outside Queue
    private final Strategy strategy; //define target
    private final List<PersonRequest> inside = new ArrayList<>(); // inside Queue
    private Status status = new Waiting(false);
    private Position currentPosition;
    private Position target;
    private boolean end = false;
    private final int capacity;
    private Direction direction;
    private Position lastPosition = new Position(Building.B, 0);

    public Elevator(int id, char building, int floor,
                    PersonQueue personQueue, int capacity, String type) {
        this.building = Building.toBuilding(building);
        this.id = id;
        //this.dispatcher = dispatcher;
        this.currentPosition = new Position(Building.toBuilding(building), floor);
        this.personQueue = personQueue;
        if (type.equals("building")) {
            this.strategy = new LookStrategyVertical(personQueue, inside);
        } else if (type.equals("floor")) {
            this.strategy = new LookStrategyHorizontal(personQueue, inside);
        } else {
            this.strategy = null;
        }

        this.capacity = capacity;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    @Override
    public void run() {
        try {
            if (strategy instanceof LookStrategyVertical) {
                sleep(400);
            } else if (strategy instanceof LookStrategyHorizontal) {
                sleep(200);
            }

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
                    //System.out.println("Elevator" + this.id + " End");
                    //outPutStream.setEnd();
                    break;
                }
            }
        }
    }

    public void passengerOut(Elevator elevator) throws InterruptedException {
        Iterator<PersonRequest> it = elevator.inside.iterator();
        while (it.hasNext()) {
            PersonRequest r = it.next();
            if (r.getToFloor() == elevator.currentPosition.getFloor() &&
                    elevator.currentPosition.getBuilding().
                            equals(Building.valueOf(String.valueOf(r.getToBuilding())))) {

                String out = "OUT-" + r.getPersonId() + "-" +
                        elevator.currentPosition.getBuilding() +
                        "-" + elevator.currentPosition.getFloor() + "-" + elevator.id + "";
                //this.outPutStream.putString(out);
                OutputThread.println(out);
                it.remove();
            }
        }
    }

    public void passengerIn(Elevator elevator) throws InterruptedException {
        /*TODO:*/
        int maxNum = elevator.capacity - elevator.inside.size();
        List<PersonRequest> addList = elevator.personQueue.getInPerson(maxNum,
                elevator.currentPosition, elevator.direction);
        for (PersonRequest r : addList) {
            String out = "IN-" + r.getPersonId() + "-" +
                    elevator.currentPosition.getBuilding() +
                    "-" + elevator.currentPosition.getFloor() + "-" + elevator.id + "";
            //elevator.outPutStream.putString(out);
            OutputThread.println(out);
            inside.add(r);
        }
    }
}

