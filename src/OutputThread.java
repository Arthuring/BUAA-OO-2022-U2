import com.oocourse.TimableOutput;

public class OutputThread {
    public static synchronized void println(String msg) {
        TimableOutput.println(msg);
    }
}
