import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.ElevatorRequest;
import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;

public class InstructionListener extends Thread {
    private boolean end = false;

    public InstructionListener() {
        this.end = false;
    }

    @Override
    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        int requestNum = 0;
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
                    Dispatcher.getInstance().addRawRequest((PersonRequest) request);
                    requestNum += 1;
                } else if (request instanceof ElevatorRequest) {
                    // an ElevatorRequest
                    // your code here
                    //System.out.println("An ElevatorRequest: " + request);
                    Dispatcher.getInstance().addElevator((ElevatorRequest) request);
                }
            }
        }
        try {
            elevatorInput.close();
            this.end = true;
            //System.out.println("Input end");
            for (int i = 0; i < requestNum; ++i) {
                RequestCounter.getInstance().acquire();
            }

            Dispatcher.getInstance().setEnd(true);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

}
