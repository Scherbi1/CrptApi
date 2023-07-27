import java.util.concurrent.TimeUnit;

public class Main {
    public static class CrptApi {
        private final int requestLimit;
        private final long intervalInMillis;
        private int requestCount;
        private long lastRequestTime;

        public CrptApi(TimeUnit timeUnit, int requestLimit) {
            this.requestLimit = requestLimit;
            this.intervalInMillis = timeUnit.toMillis(1);
            this.requestCount = 0;
            this.lastRequestTime = System.currentTimeMillis();
        }

        public synchronized void createDocument(Object document, String signature) {
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastRequestTime >= intervalInMillis) {
                // Reset the request count if the interval has passed
                requestCount = 0;
                lastRequestTime = currentTime;
            }

            if (requestCount >= requestLimit) {
                // If the request limit has been reached, wait until the interval has passed
                try {
                    long remainingTime = intervalInMillis - (currentTime - lastRequestTime);
                    TimeUnit.MILLISECONDS.sleep(remainingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Reset the request count and last request time after waiting
                requestCount = 0;
                lastRequestTime = System.currentTimeMillis();
            }

            // Process the document creation here
            System.out.println("Creating document: " + document);
            System.out.println("Signature: " + signature);

            // Increment the request count
            requestCount++;
        }

        public static void main(String[] args) {
            CrptApi crptApi = new CrptApi(TimeUnit.SECONDS, 3);

            // Simulate multiple requests
            for (int i = 1; i <= 5; i++) {
                final int documentNumber = i;
                new Thread(() -> {
                    crptApi.createDocument("Document " + documentNumber, "Signature " + documentNumber);
                }).start();
            }
        }
    }
}