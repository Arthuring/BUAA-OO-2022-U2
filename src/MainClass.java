import com.oocourse.TimableOutput;

import java.util.ArrayList;

public class MainClass {
    public static void main(String[] args) throws Exception {
        TimableOutput.initStartTimestamp();
        Dispatcher dispatcher = new Dispatcher();
        InstructionListener instructionListener = new InstructionListener(dispatcher);
        instructionListener.start();
        ArrayList<Elevator> elevatorsList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            char building = (char) ('A' + i);
            Elevator elevator = new Elevator(i + 1, building,1,
                    dispatcher.getPersonQueueListBuilding().get(String.valueOf(building)),
                    6, "building");
            elevatorsList.add(elevator);
        }
        dispatcher.setElevators(elevatorsList);
        for (int i = 0; i < 5; i++) {
            elevatorsList.get(i).start();
        }
    }
}
