public class RequestCounter {
    private int requestCount;
    private static final RequestCounter COUNTER = new RequestCounter();

    RequestCounter() {
        requestCount = 0;
    }

    public static RequestCounter getInstance() {
        return COUNTER;
    }

    public synchronized void release() {
        requestCount = requestCount + 1;
        notifyAll();
    }

    public synchronized void acquire() {
        while (true) {
            if (requestCount > 0) {
                requestCount -= 1;
                break;
            } else {
                try {
                    wait();
                } catch (InterruptedException e) {
                    //
                }

            }
        }
    }
}
