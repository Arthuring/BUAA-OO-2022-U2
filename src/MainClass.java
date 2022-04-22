import com.oocourse.TimableOutput;

import java.util.ArrayList;

public class MainClass {
    public static void main(String[] args) throws Exception {
        TimableOutput.initStartTimestamp();
        ArrayList<Elevator> elevatorsList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            char building = (char) ('A' + i);
            Elevator elevator = new Elevator(i + 1, building, 1,
                    Dispatcher.getInstance().getPersonQueueListBuilding()
                            .get(String.valueOf(building)),
                    8, "building");
            elevatorsList.add(elevator);
        }
        Elevator elevator = new Elevator(6, 'A', 1,
                Dispatcher.getInstance().getPersonQueueListFloor().get(1), 8, "floor");
        elevatorsList.add(elevator);
        Dispatcher.getInstance().setElevators(elevatorsList);
        for (int i = 0; i < 6; i++) {
            elevatorsList.get(i).start();
        }
        InstructionListener instructionListener = new InstructionListener();
        instructionListener.start();
    }
}
