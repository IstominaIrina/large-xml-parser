import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.*;

public class BlockingQueueTest {
    String fileName = "res/data-1572M.xml";

    private BlockingQueue<StringBuilder> q;

    public BlockingQueueTest() throws InterruptedException, SQLException {
        long start = System.currentTimeMillis();

        q = new ArrayBlockingQueue<StringBuilder>(5, true);
        Producer p = new Producer(q, fileName);
        Thread tProd = new Thread(p);
        tProd.start();

        DBConnection.getConnection();

        ArrayList<Thread> threadList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Consumer c = new Consumer(q, p, i);
            Thread tCons = new Thread(c);
            threadList.add(tCons);
        }

        for (int i = 0; i < threadList.size(); i++) {
            threadList.get(i).start();
        }

        tProd.join();

        for (int i = 0; i < threadList.size(); i++) {
            threadList.get(i).join();
        }

        System.out.println("Общее время работы - " + ((System.currentTimeMillis() - start)) + " ms.");

    }
}
