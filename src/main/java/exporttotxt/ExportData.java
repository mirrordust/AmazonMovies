package exporttotxt;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class ExportData {

    public static void main(String[] args) throws IOException {

        String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        String DB_URL1 = "jdbc:mysql://127.0.0.1:3306/amazon_movies";
        String USER1 = "root";
        String PASS1 = "root";

        String productId = "";

        Connection conn = null;

        Statement stmt = null;

        PreparedStatement pstmt = null;

        ResultSet resultSet = null;

        // amazon_movies
        String select_sql1 = "SELECT movie_name, director, starring, actor, style FROM movie";
        String select_sql2 = "SELECT movie_name, director_name FROM director";
        String select_sql3 = "SELECT movie_name, protagonist_name FROM protagonist";
        String select_sql4 = "SELECT movie_name, actor_name FROM actor";
        String select_sql5 = "SELECT movie_name, style FROM movie_style";
        String select_sql6 = "SELECT product_id, movie_name, format, date, year, month, day, quarter, day_of_week FROM movie_format";
        String select_sql7 = "SELECT review_id, product_id, user_id, profilename, " +
                "helpfulness, score, time, summary, text FROM review " +
                "WHERE review_id BETWEEN ? AND ?";
        String range = "SELECT MIN(review_id) AS iid, MAX(review_id) AS aid FROM review";

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL1, USER1, PASS1);
            if (conn == null) {
                System.out.println("connection fail!");
                return;
            }
            conn.setAutoCommit(false);
            System.out.println("connection successfully!");

            stmt = conn.createStatement();

            File file = null;
            FileWriter fileWriter = null;
            BufferedWriter bufferedWriter = null;

            // 1.movie
            resultSet = stmt.executeQuery(select_sql1);
            file = new File("movie.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file.getName(), true);
            bufferedWriter = new BufferedWriter(fileWriter);
            while (resultSet.next()) {
                bufferedWriter.write(resultSet.getString(1)+'\001'
                +resultSet.getString(2)+'\001'
                +resultSet.getString(3)+'\001'
                +resultSet.getString(4)+'\001'
                +resultSet.getString(5)+"\r\n");
            }
            bufferedWriter.close();
            System.out.println("movie finish.");
            // 2.director
            resultSet = stmt.executeQuery(select_sql2);
            file = new File("director.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file.getName(), true);
            bufferedWriter = new BufferedWriter(fileWriter);
            while (resultSet.next()) {
                bufferedWriter.write(resultSet.getString(1)+'\001'
                +resultSet.getString(2)+"\r\n");
            }
            bufferedWriter.close();
            System.out.println("director finish.");
            // 3.protagonist
            resultSet = stmt.executeQuery(select_sql3);
            file = new File("protagonist.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file.getName(), true);
            bufferedWriter = new BufferedWriter(fileWriter);
            while (resultSet.next()) {
                bufferedWriter.write(resultSet.getString(1)+'\001'
                        +resultSet.getString(2)+"\r\n");
            }
            bufferedWriter.close();
            System.out.println("protagonist finish.");
            // 4.actor
            resultSet = stmt.executeQuery(select_sql4);
            file = new File("actor.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file.getName(), true);
            bufferedWriter = new BufferedWriter(fileWriter);
            while (resultSet.next()) {
                bufferedWriter.write(resultSet.getString(1)+'\001'
                        +resultSet.getString(2)+"\r\n");
            }
            bufferedWriter.close();
            System.out.println("actor finish.");
            // 5.style
            resultSet = stmt.executeQuery(select_sql5);
            file = new File("movie_style.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file.getName(), true);
            bufferedWriter = new BufferedWriter(fileWriter);
            while (resultSet.next()) {
                bufferedWriter.write(resultSet.getString(1)+'\001'
                        +resultSet.getString(2)+"\r\n");
            }
            bufferedWriter.close();
            System.out.println("style finish.");
            // 6.format
            resultSet = stmt.executeQuery(select_sql6);
            file = new File("movie_format.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file.getName(), true);
            bufferedWriter = new BufferedWriter(fileWriter);
            while (resultSet.next()) {
                bufferedWriter.write(resultSet.getString(1)+'\001'
                        +resultSet.getString(2)+'\001'
                        +resultSet.getString(3)+'\001'
                        +resultSet.getInt(4)+'\001'
                        +resultSet.getInt(5)+'\001'
                        +resultSet.getInt(6)+'\001'
                        +resultSet.getInt(7)+'\001'
                        +resultSet.getInt(8)+'\001'
                        +resultSet.getInt(9) +"\r\n");
            }
            bufferedWriter.close();
            System.out.println("format finish.");

            // 7.review
            resultSet = stmt.executeQuery(range);
            int minId = 0, maxId = 0;
            while (resultSet.next()) {
                minId = resultSet.getInt(1);
                maxId = resultSet.getInt(2);
            }

            pstmt = conn.prepareStatement(select_sql7);

            int beginId = minId;
            int interval = 1000000;
            int endId = (maxId - beginId > interval ? beginId + interval : maxId);
            while (endId <= maxId) {
                pstmt.setInt(1, beginId);
                pstmt.setInt(2, endId);
                resultSet = pstmt.executeQuery();
                file = new File("review.txt");
                if (!file.exists()) {
                    file.createNewFile();
                }
                fileWriter = new FileWriter(file.getName(), true);
                bufferedWriter = new BufferedWriter(fileWriter);
                while (resultSet.next()) {
                    bufferedWriter.write(resultSet.getInt(1)+'\001'
                            +resultSet.getString(2)+'\001'
                            +resultSet.getString(3)+'\001'
                            +resultSet.getString(4)+'\001'
                            +resultSet.getString(5)+'\001'
                            +resultSet.getDouble(6)+'\001'
                            +resultSet.getBigDecimal(7)+'\001'
                            +resultSet.getString(8)+'\001'
                            +resultSet.getString(9)+"\r\n");
                }
                bufferedWriter.close();
                System.out.println("one round finish.");
                if (maxId == endId) {
                    break;
                }
                beginId = endId + 1;
                endId = (maxId - beginId > interval ? beginId + interval : maxId);
            }


            System.out.println("review finish.");

            resultSet.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                assert stmt != null;
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

}
