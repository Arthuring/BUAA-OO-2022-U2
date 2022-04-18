import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.ElevatorRequest;
import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;

public class InstructionListener extends Thread {
    private final Dispatcher dispatcher;
    private boolean end = false;

    public InstructionListener(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            Request request = elevatorInput.nextRequest();
            // when request == null
            // it means there are no more lines in stdin
            if (request == null) {
                break;
            } else {
                // a new valid request
                if (request instanceof PersonRequest) {
                    // a PersonRequest
                    // your code here
                    //System.out.println("A PersonRequest:    " + request);
                    dispatcher.addPersonRequest((PersonRequest)request);
                } else if (request instanceof ElevatorRequest) {
                    // an ElevatorRequest
                    // your code here
                    //System.out.println("An ElevatorRequest: " + request);
                    dispatcher.addElevator((ElevatorRequest) request);
                }
            }
        }
        try {
            elevatorInput.close();
            this.end = true;
            //System.out.println("Input end");
            dispatcher.setEnd(true);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

}
