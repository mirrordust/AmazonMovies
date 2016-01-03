package parsehtml;


import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class Parser2 {

    public static void main(String[] args) throws IOException {

        String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        String DB_URL1 = "jdbc:mysql://127.0.0.1:3306/amazon_movies";
        String USER1 = "root";
        String PASS1 = "root";

        String productId = "";

        int repeatCount = 0;

        //文件名
        String infilePathPrefix = "E:\\datawarehouse\\HTMLs\\";
        String infilePathPostfix = ".html";

        Connection conn = null;

        Statement statement = null;
        ResultSet resultSet = null;
        //PreparedStatements
        PreparedStatement upstmt = null;

        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;
        PreparedStatement stmt3 = null;
        PreparedStatement stmt4 = null;
        PreparedStatement stmt5 = null;
        PreparedStatement stmt6 = null;


        //String select_sql = "SELECT * FROM processed_id WHERE isDealt = 0 and typex = 2";
        String select_sql = "SELECT * FROM processed_id WHERE isDealt = 0 and typex = 2";
        String update_sql = "UPDATE processed_id SET isDealt = 1 WHERE id = ?";

        String insert_sql1 = "INSERT INTO movie VALUES (?,?,?,?,?)";
        String insert_sql2 = "INSERT INTO director VALUES (?,?)";
        String insert_sql3 = "INSERT INTO protagonist VALUES (?,?)";
        String insert_sql4 = "INSERT INTO actor VALUES (?,?)";
        String insert_sql5 = "INSERT INTO movie_style VALUES (?,?)";
        String insert_sql6 = "INSERT INTO movie_format VALUES (?,?,?,?,?,?,?,?,?)";

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL1, USER1, PASS1);
            if (conn == null) {
                System.out.println("connection fail!");
                return;
            }
            conn.setAutoCommit(false);
            System.out.println("connection successfully!");

            statement = conn.createStatement();
            resultSet = statement.executeQuery(select_sql);

            upstmt = conn.prepareStatement(update_sql);

            stmt1 = conn.prepareStatement(insert_sql1);
            stmt2 = conn.prepareStatement(insert_sql2);
            stmt3 = conn.prepareStatement(insert_sql3);
            stmt4 = conn.prepareStatement(insert_sql4);
            stmt5 = conn.prepareStatement(insert_sql5);
            stmt6 = conn.prepareStatement(insert_sql6);

            String id = "";
            Document doc = null;
            while (resultSet.next()) {
                String movieName = "";
                ArrayList<String> directors = new ArrayList<String>();
                ArrayList<String> starrings = new ArrayList<String>();
                ArrayList<String> actors = new ArrayList<String>();
                ArrayList<String> styles = new ArrayList<String>();
                String format = "";
                String date = "";

                id = resultSet.getString(1);
                productId = id;
                doc = Jsoup.parse(new File(infilePathPrefix + id + infilePathPostfix), "UTF-8");
                //提取数据
                //movie name
                movieName = doc.getElementById("aiv-content-title").text();
                movieName = movieName.replaceAll(" \\([a-z0-9A-Z ,./<>?;':\\|!@#\\$%\\^&\\*-=_+`~\"]*\\)", "");
                movieName = movieName.replaceAll(" \\[[a-z0-9A-Z ,./<>?;':\\|!@#\\$%\\^&\\*-=_+`~\"]*\\]", "").trim();
                Elements t_elements = doc.select("h1#aiv-content-title > span");
                for (Element e : t_elements) {
                    movieName = movieName.replace(e.text(), "").trim();
                }
                if (movieName.length() > 255) {
                    movieName = movieName.substring(0, 255);
                }

                //date(year)
                t_elements = doc.select("h1#aiv-content-title > span.release-year");
                for (Element e : t_elements) {
                    date = e.text().trim();
                }


                //details
                Elements elements = doc.select("div#dv-center-features tr");
                for (Element e : elements) {
                    //style
                    if (e.text().contains("Genres")) {
                        String[] ss1 = e.text().replace("Genres", "").split(",");
                        for (String s : ss1) {
                            styles.add(s.trim());
                        }
                    }
                    //director
                    if (e.text().contains("Director")) {
                        String[] ss1 = e.text().replace("Director", "").split(",");
                        for (String s : ss1) {
                            directors.add(s.trim());
                        }
                    }
                    //star
                    if (e.text().contains("Starring")) {
                        String[] ss1 = e.text().replace("Starring", "").split(",");
                        for (String s : ss1) {
                            starrings.add(s.trim());
                            actors.add(s.trim());
                        }
                    }
                    //support actor
                    if (e.text().contains("Supporting actors")) {
                        String[] ss1 = e.text().replace("Supporting actors", "").split(",");
                        for (String s : ss1) {
                            actors.add(s.trim());
                        }
                    }
                    //format
                    if (e.text().contains("Format")) {
                        String[] ss1 = e.text().replace("Format", "").split(",");
                        format = ss1[0].trim();
                    }
                }

                try {
                    stmt1.setString(1, movieName);
                    String t = "";
                    for (int i = 0; i < directors.size(); i++) {
                        if (i == 0) {
                            t += directors.get(i);
                        } else {
                            t += (", " + directors.get(i));
                        }
                    }
                    stmt1.setString(2, t);
                    t = "";
                    for (int i = 0; i < starrings.size(); i++) {
                        if (i == 0) {
                            t += starrings.get(i);
                        } else {
                            t += (", " + starrings.get(i));
                        }
                    }
                    stmt1.setString(3, t);
                    t = "";
                    for (int i = 0; i < actors.size(); i++) {
                        if (i == 0) {
                            t += actors.get(i);
                        } else {
                            t += (", " + actors.get(i));
                        }
                    }
                    stmt1.setString(4, t);
                    t = "";
                    for (int i = 0; i < styles.size(); i++) {
                        if (i == 0) {
                            t += styles.get(i);
                        } else {
                            t += (", " + styles.get(i));
                        }
                    }
                    stmt1.setString(5, t);
                    stmt1.execute();

                    for (String s : directors) {
                        stmt2.setString(1, movieName);
                        stmt2.setString(2, s);
                        stmt2.execute();
                    }

                    for (String s : starrings) {
                        stmt3.setString(1, movieName);
                        stmt3.setString(2, s);
                        stmt3.execute();
                    }

                    for (String s : actors) {
                        stmt4.setString(1, movieName);
                        stmt4.setString(2, s);
                        stmt4.execute();
                    }

                    for (String s : styles) {
                        stmt5.setString(1, movieName);
                        stmt5.setString(2, s);
                        stmt5.execute();
                    }

                    stmt6.setString(1, id);
                    stmt6.setString(2, movieName);
                    stmt6.setString(3, format);
                    if (date.equals("")) {
                        stmt6.setInt(4, 0);
                        stmt6.setInt(5, 0);
                        stmt6.setInt(6, 0);
                        stmt6.setInt(7, 0);
                        stmt6.setInt(8, 0);
                        stmt6.setInt(9, 0);
                    } else {
                        stmt6.setInt(4, Integer.parseInt(date)*10000);
                        stmt6.setInt(5, Integer.parseInt(date));
                        stmt6.setInt(6, 0);
                        stmt6.setInt(7, 0);
                        stmt6.setInt(8, 0);
                        stmt6.setInt(9, 0);
                        /*java.util.Date date1 = new MyTime().formatTime(date);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date1);
                        stmt6.setInt(4, calendar.get(Calendar.YEAR)*10000+
                                calendar.get(Calendar.MONTH)*100+100 +
                                calendar.get(Calendar.DAY_OF_MONTH));
                        stmt6.setInt(5, calendar.get(Calendar.YEAR));
                        stmt6.setInt(6, calendar.get(Calendar.MONTH) + 1);
                        stmt6.setInt(7, calendar.get(Calendar.DAY_OF_MONTH));
                        int quarter = 0;
                        if (calendar.get(Calendar.MONTH) >= 0 && calendar.get(Calendar.MONTH) <=2) {
                            quarter = 1;
                        } else if (calendar.get(Calendar.MONTH) >= 3 && calendar.get(Calendar.MONTH) <= 5) {
                            quarter = 2;
                        } else if (calendar.get(Calendar.MONTH) >= 6 && calendar.get(Calendar.MONTH) <= 8) {
                            quarter = 3;
                        } else {
                            quarter = 4;
                        }
                        stmt6.setInt(8 ,quarter);
                        stmt6.setInt(9, calendar.get(Calendar.DAY_OF_WEEK) - 1);*/
                    }
                    stmt6.execute();

                    upstmt.setString(1, id);
                    upstmt.execute();

                    conn.commit();

                } catch (MySQLIntegrityConstraintViolationException e) {
                    conn.rollback();
                    stmt6.setString(1, id);
                    stmt6.setString(2, movieName);
                    stmt6.setString(3, format);
                    if (date.equals("")) {
                        stmt6.setInt(4, 0);
                        stmt6.setInt(5, 0);
                        stmt6.setInt(6, 0);
                        stmt6.setInt(7, 0);
                        stmt6.setInt(8, 0);
                        stmt6.setInt(9, 0);
                    } else {
                        stmt6.setInt(4, Integer.parseInt(date)*10000);
                        stmt6.setInt(5, Integer.parseInt(date));
                        stmt6.setInt(6, 0);
                        stmt6.setInt(7, 0);
                        stmt6.setInt(8, 0);
                        stmt6.setInt(9, 0);
                    }
                    stmt6.execute();
                    //System.out.println("重复");
                    upstmt.setString(1, id);
                    upstmt.execute();
                    conn.commit();
                    repeatCount++;
                }

                /*System.out.println("===========================================");
                System.out.println("id:"+id);
                System.out.println("name:"+movieName);
                System.out.print("dir:");
                for (String s : directors) {
                    System.out.print(s + ", ");
                }
                System.out.println("");
                System.out.print("star:");
                for (String s : starrings) {
                    System.out.print(s + ", ");
                }
                System.out.println("");
                System.out.print("actor:");
                for (String s : actors) {
                    System.out.print(s + ", ");
                }
                System.out.println("");
                System.out.print("style:");
                for (String s : styles) {
                    System.out.print(s + ", ");
                }
                System.out.println("");
                System.out.println("format:"+format);
                System.out.println("date:"+ (date.equals("") ? "0000" : Integer.parseInt(date)));
                System.out.println();
                System.out.println();
                System.out.println("===========================================");*/
            }


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("id: " + productId);
            File file = new File("Exception.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file.getName(), true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(productId + "\r\n");
            bufferedWriter.close();
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("id: " + productId);
            File file = new File("Exception2.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file.getName(), true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("\r\n");
            bufferedWriter.close();
            e.printStackTrace();
        }
        finally {
            try {
                System.out.println("重复数目: " + repeatCount);
                assert upstmt != null;
                upstmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

}
