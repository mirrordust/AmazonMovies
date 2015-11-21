import java.sql.*;
import java.util.ArrayList;

public class Cleaning {

    public static void main(String[] args) throws ClassNotFoundException {

        ArrayList<String> names = new ArrayList<String>();
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement insertStatement = null;

        Class.forName("com.mysql.jdbc.Driver");

        String selectQuery = "select * from movienames";

        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/amazonmovies", "root", "root");
            if (!conn.isClosed())
                System.out.println("Success to connect to MySQL");
            else
                System.out.println("Fail to connect to MySQL");
            statement = conn.createStatement();
            insertStatement = conn.prepareStatement("insert into cleaning values (?)");
            resultSet = statement.executeQuery(selectQuery);

            while (resultSet.next()) {
                names.add(resultSet.getString(1));
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException sqlEx) { } // ignore
                resultSet = null;
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException sqlEx) { } // ignore
                statement = null;
            }
        }

        /* NR
           CC
           PG-13
           G
           R
          []
          ()
          DVD
          Vol. 3
          - With CD Soundtrack #3

        */

        for (String name : names) {
            name = name.replaceAll(" \\([a-z0-9A-Z ,./<>?;':\\|!@#\\$%\\^&\\*-=_+`~\"]*\\)", "");
            name = name.replaceAll(" \\[[a-z0-9A-Z ,./<>?;':\\|!@#\\$%\\^&\\*-=_+`~\"]*\\]", "");
            name = name.replaceAll("\\d{4} NR", "");
            name = name.replaceAll("\\d{4} CC", "");
            name = name.replaceAll("\\d{4} G", "");
            name = name.replaceAll("\\d{4} R", "");
            name = name.replaceAll("\\d{4} PG-\\d*", "");
            name = name.replaceAll("\\d{4} Unrated", "");
            name = name.replaceAll("\\d{4} Movie", "");
            name = name.replaceAll("\\d{4} PG", "");
            name = name.replaceAll(" 3D", "");
            name = name.replaceAll(" Movie", "");
            name = name.replaceAll(" NR", "");
            name = name.replaceAll(" CC", "");
            name = name.replaceAll(" G", "");
            name = name.replaceAll(" R", "");
            name = name.replaceAll(" PG", "");
            name = name.replaceAll(" PG-\\d*", "");
            name = name.replaceAll(" Unrated", "");
            name = name.replaceAll("DVD", "");
            name = name.replaceAll("Vol. \\d+", "");
            name = name.replaceAll("- With CD Soundtrack #\\d*", "");
            name = name.replaceAll(" ", "");
            name = name.replaceAll("-", "");
            name = name.replaceAll(":", "");
            name = name.replaceAll(",", "");
            name = name.replaceAll("\"", "");
            name = name.replaceAll("Season\\d+.*", "");
            name = name.replaceAll("\\d*Seasons\\d+.*", "");
            name = name.replaceAll("Series\\d+", "");
            name = name.replaceAll("Set\\d+.*", "");
            name = name.replaceAll("BoxSet\\d+.*", "");
            name = name.replaceAll("Vol\\d+.*", "");
            name = name.replaceAll("Volume.*", "");
            name = name.replaceAll("Part\\d+.*", "");
            // name = name.replaceAll("\\[ NR CC G R PG-\\d*DVDVol. \\d*- With CD Soundtrack #\\d*\\]", "");
            try {
                assert insertStatement != null;
                insertStatement.setString(1, name);
                insertStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

}
