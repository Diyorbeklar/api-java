import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CrptApi {
    private final Lock lock = new ReentrantLock();
    private final int requestLimit;
    private final long timeInterval;
    private long lastRequestTime = System.currentTimeMillis();
    private int requestsCount = 0;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.requestLimit = requestLimit;
        this.timeInterval = timeUnit.toMillis(1);
    }

    public void createDocument(Object document, String signature) {
        try {
            lock.lock();
            long currentTime = System.currentTimeMillis();
            
            if (currentTime - lastRequestTime > timeInterval) {
 
                requestsCount = 0;
                lastRequestTime = currentTime;
            }
            
            if (requestsCount >= requestLimit) {
     
                long sleepTime = lastRequestTime + timeInterval - currentTime;
                Thread.sleep(sleepTime);
                lastRequestTime += timeInterval;
                requestsCount = 1;
            } else {
                requestsCount++;
            }
            
            performApiCall(document, signature);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    private void performApiCall(Object document, String signature) {

        System.out.println("API call with document: " + document.toString() + ", signature: " + signature);
    }
    
    public static void main(String[] args) {
        CrptApi crptApi = new CrptApi(TimeUnit.SECONDS, 3);

        for (int i = 0; i < 10; i++) {
            final int count = i;
            new Thread(() -> crptApi.createDocument("Document " + count, "Signature " + count)).start();
        }
    }
}

