/**
 * Created by Dmitry Shatalov on 04/11/16.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Repository {

    private Connection conn;

    public Repository () throws IOException, SQLException, ClassNotFoundException {

        // Read user's login and password
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String login = "";
        String password = "";
        while (!Pattern.matches("\\w{8}", login) || !Pattern.matches("cdm" + "\\d{7}", password)) {
            System.out.println("Provide the login: ");
            login = reader.readLine();
            System.out.println("Provide the password: (cdm{your id})");
            password = reader.readLine();
        }
        // Establish connection with the Oracle database
        Class.forName("oracle.jdbc.driver.OracleDriver");
        conn = DriverManager.getConnection("jdbc:oracle:thin:@140.192.30.237 :1521:def", login, password);
    }


    /* Returns all flights for today */

    public void getSchedule() throws SQLException {

        String sql = "SELECT FLIGHT_NUMBER, CITY_DESTINATION, COUNTRY_DESTINATION, FLIGHT_TIME FROM SCHEDULE WHERE FLIGHT_TIME > (SYSDATE - 1)";
        Statement sqlStmt = conn.createStatement();
        ResultSet rs = sqlStmt.executeQuery(sql);
        while(rs.next())
        {
            String flightNumber = rs.getString("FLIGHT_NUMBER");
            String city = rs.getString("CITY_DESTINATION");
            String country = rs.getString("COUNTRY_DESTINATION");
            Timestamp time = rs.getTimestamp("FLIGHT_TIME");

            System.out.printf("%s %10s %10s %12s\n", flightNumber, city, country, time.toString());
        }
    }

    /* Returns all the services assigned to the flights */

    public void getAssignedServices() throws SQLException {
        String sql = "SELECT r.FLIGHT_NUMBER, r.cargo_service, k.UNIT_NUMBER food_service FROM (SELECT f.FLIGHT_NUMBER, g.UNIT_NUMBER cargo_service " +
                "FROM  SCHEDULE f LEFT JOIN CARGO_SERVE g ON g.flight_number = f.flight_number) r " +
                "LEFT JOIN FOOD_SERVE k ON k.flight_number = r.flight_number";
        Statement sqlStmt = conn.createStatement();
        ResultSet rs = sqlStmt.executeQuery(sql);
        while(rs.next())
        {
            String flightNumber = rs.getString("FLIGHT_NUMBER");
            int cargoService = rs.getInt("CARGO_SERVICE");
            int foodService = rs.getInt("FOOD_SERVICE");

            System.out.printf("%s %5d %5d\n", flightNumber, cargoService, foodService);
        }
    }

    public void insertFlight(String flightNumber, String airline, Timestamp time, String city, String country) throws SQLException {

        // Gets Airline id of the specified airline
        String sql = "SELECT AIRLINE_ID FROM AIRLINE WHERE AIRLINE_NAME = " + airline;
        Statement sqlStmt = conn.createStatement();
        ResultSet rs = sqlStmt.executeQuery(sql);
        rs.next();
        int airlineId = rs.getInt("AIRLINE_ID");

        conn.setAutoCommit(false);

        PreparedStatement insertFlight;

        String insertSQL = "INSERT INTO Schedule " +
                "VALUES " +
                "(?, ?, ?, ?, ?, ?, ?)";

        insertFlight = conn.prepareStatement(insertSQL);

        insertFlight.setString(1, flightNumber);
        insertFlight.setInt(2, airlineId);
        insertFlight.setTimestamp(3, time);
        insertFlight.setString(4, city);
        insertFlight.setString(5,  country);
        insertFlight.setString(6, "2");
        insertFlight.setString(7, "A320");


        insertFlight.executeUpdate();
        conn.commit();

    }
}
