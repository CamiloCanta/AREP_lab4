import org.junit.Test;

import java.io.IOException;

import static edu.escuelaing.arep.app.Client.testServerFunctionality;


public class AppTest {

    @Test
    public void testConcurrentRequest(){
        int numThreads = 10;

        for (int i = 0; i < numThreads; i++) {
            Thread thread = new Thread(() -> {
                try {
                    testServerFunctionality();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
