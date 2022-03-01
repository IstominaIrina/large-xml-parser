
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

public class XMLHandler extends DefaultHandler {

    int limit = 500_000;
    int number = 0;
    private BlockingQueue queue;

    private Voter voter;
    private static SimpleDateFormat birthDayFormat = new SimpleDateFormat("yyyy.MM.dd");
    private HashMap<Voter, Short> voterCounts;

    public XMLHandler(BlockingQueue queue) {
        voterCounts = new HashMap<>();
        this.queue = queue;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws
            SAXException {
        if (qName.equals("voter") && number < limit) {
            String name = attributes.getValue("name");
            String birthDate = attributes.getValue("birthDay");
            try {
                DBConnection.countVoter(name, birthDate);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            number++;
        }

        if (number == limit) {
            try {
                this.queue.put(DBConnection.getInsertQuery());
                System.out.println("1 - положили в очередь - " + DBConnection.getInsertQuery().length());
                DBConnection.clear();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            number = 0;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("voter")) {
            voter = null;
        }
    }
}
