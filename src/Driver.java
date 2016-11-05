
import java.io.IOException;
import java.sql.*;


/**
 * Created by Shatalov on 04/11/16.
 */
public class Driver {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Repository r;

        try {
            r = new Repository();
            r.getSchedule();
            r.getAssignedServices();
            r.insertFlight("UN3456", "'UNITED AIRLINES'", Timestamp.valueOf("2016-11-04 20:30:00"), "Pittsburgh", "USA");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
