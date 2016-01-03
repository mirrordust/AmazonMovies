package amazonmovies;

import java.sql.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Main {

    public final static String hosts[] = {"222.39.64.13","58.23.16.245","180.169.106.4","q.gfw.li","122.76.249.2",
            "117.177.243.42","39.189.106.164","222.74.212.163","122.96.59.106","163.177.155.2",
            "91.183.124.41","154.70.134.74","112.74.26.237","218.28.90.118","124.193.7.247",
            "111.13.109.52","203.195.162.96","118.193.219.93","180.166.56.47","117.165.243.253"};
    public final static String ports[] = {"8118","80","8080","40999","8118",
            "8080","8123","3128","83","8088",
            "80","8080","80","3128","3128",
            "80","80","80","80","8123"};

    /*public final static String hosts[] = {"127.141.8.1", "185.56.88.152", "177.52.40.3", "198.100.144.173", "118.88.22.139", "200.113.13.77", "122.0.80.220", "211.167.105.69", "222.39.64.13", "183.252.18.131", "222.45.96.231", "122.89.24.6", "182.92.155.193", "58.23.16.245", "119.6.200.210", "123.57.133.112", "123.129.203.87",
            "1.34.41.181",
            "120.24.175.25",
            "58.220.2.140"};
    public final static String ports[] = {"80", "80", "8080", "3128", "80", "80", "80", "83", "8118", "8080", "8118", "8118", "8080", "80", "80", "8080", "80",
            "80",
            "80",
            "80"};*/


    public static void main(String[] args) throws ClassNotFoundException {

        //代理
        /*System.getProperties().setProperty("socksProxySet", "true");
        System.getProperties().setProperty("socksProxyHost", "127.0.0.1");
        System.getProperties().setProperty("socksProxyPort", "1080");*/

        /*Properties prop = System.getProperties();
        prop.setProperty("http.proxyHost", "122.226.142.59");
        prop.setProperty("http.proxyPort", "3128");*/

        Queue<String> productIds = new ConcurrentLinkedQueue<String>();
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement insertStatement = null;
        PreparedStatement updateStatement = null;

        Class.forName("com.mysql.jdbc.Driver");

        String selectQuery = "select * from productids " +
                "where productId is not null " +
                "and " +
                "productId not like '%Who is%' " +
                "and isDealt = 0";

        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/amazon_movies", "root", "root");
            if (!conn.isClosed())
                System.out.println("Success to connect to MySQL");
            else
                System.out.println("Fail to connect to MySQL");
            statement = conn.createStatement();
            insertStatement = conn.prepareStatement("insert  into movienames values (?)");
            updateStatement = conn.prepareStatement("replace into productids values(?,1)");
            resultSet = statement.executeQuery(selectQuery);

            while (resultSet.next()) {
                productIds.add(resultSet.getString(1));
            }

            System.out.println("productIds.size:" + productIds.size());

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



        Crawler.create(productIds, insertStatement, updateStatement)
                .thread(200)
                .start();

    }

}
