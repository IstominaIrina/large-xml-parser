import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.concurrent.BlockingQueue;

public class Producer implements Runnable {

    String fileName;
    BlockingQueue queue;
    boolean runFlag;

    Producer(BlockingQueue q, String fileName) {
        this.queue = q;
        this.fileName = fileName;
        this.runFlag = true;
    }

    @Override
    public void run() {
        try {

            this.parseFileSAX();

            if (DBConnection.getInsertQuery() != null) {
                System.out.println(" 1 - положила в очередь - " + DBConnection.getInsertQuery().length());
                queue.put(DBConnection.getInsertQuery());
                DBConnection.clear();
            }
            runFlag = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    void parseFileSAX() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        XMLHandler handler = new XMLHandler(this.queue);
        parser.parse(new File(this.fileName), handler);
    }
}
