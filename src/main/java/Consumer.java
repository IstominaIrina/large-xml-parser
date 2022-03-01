
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable {
    private final BlockingQueue queue;
    int name;

    Producer producer;

    Consumer(BlockingQueue q, Producer producer, int name) {
        this.queue = q;
        this.producer = producer;
        this.name = name;
    }

    @Override
    public void run() {
        while (true) {
            StringBuilder msg = null;
            msg = (StringBuilder) queue.poll();

            if (msg != null) {
                System.out.println("-1  загрузили в БД - " + msg.length() );
                try {
                 //   System.out.println("Вставка в БД, имя потока - " + name);
                    DBConnection.executeMultiInsert(msg);

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } else {
                if (this.producer.runFlag == true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else break;
            }
        }
    }
}
