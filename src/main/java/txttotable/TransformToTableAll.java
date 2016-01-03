package txttotable;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransformToTableAll {

    public static void main(String[] args) throws IOException {

        String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        String DB_URL1 = "jdbc:mysql://127.0.0.1:3306/amazon_movies";
        String USER1 = "root";
        String PASS1 = "root";


        //文件名
        String filePath = "D:\\GitHub\\Documents\\AmazonMovies\\文件名";
        int lineCount = 0;
        int totalCount = 0;

        String splitor = ""+'\001';

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL1, USER1, PASS1);
            if (conn == null) {
                System.out.println("connection fail!");
                return;
            }
            conn.setAutoCommit(false);
            System.out.println("connection successfully!");

            String sql = "INSERT INTO moviereviews_表名(productId, userId, profileName, helpfulness, " +
                    "score, time, summary, text) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            stmt = conn.prepareStatement(sql);

            File filename = new File(filePath); // 读取以上路径的input文件
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言

            String line = "";
            String[] ss = null;
            int batchCount = 0;

            int k = 100;

            while (true) {

                line = br.readLine();
                lineCount++;
                if (line == null)
                    break;
                else {
                    ss = line.split(splitor);
                    stmt.setString(1, ss[0]);
                    stmt.setString(2, ss[1]);
                    stmt.setString(3, ss[2]);
                    stmt.setString(4, ss[3]);
                    stmt.setFloat(5, Float.parseFloat(ss[4]));
                    stmt.setString(6, ss[5]);
                    stmt.setString(7, ss[6]);
                    stmt.setString(8, ss[7]);

                    if (k-- > 0) {
                        for (String sss : ss) {
                            System.out.print(sss);
                            System.out.print(", ");
                        }
                        System.out.println("  ");
                    }

                    stmt.addBatch();
                    batchCount++;
                    totalCount++;

                    if (batchCount == 10000) {
                        stmt.executeBatch();
                        conn.commit();
                        //重置batchCount为0
                        batchCount = 0;
                        if (totalCount % 20000 == 0)
                            System.out.println("Now totalCount :" + totalCount);
                    }
                }
            }

            stmt.executeBatch();
            conn.commit();
            System.out.println("Now totalCount :" + totalCount);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            File file = new File("Exception.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file.getName(), true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("SQL Exception occurs, in" + filePath +
                    ", exception row number is :" + lineCount + ", Now totalCount is " + totalCount + "\r\n");
            bufferedWriter.close();
            e.printStackTrace();
        } catch (Exception e) {
            File file = new File("Exception2.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file.getName(), true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("Other Exception occurs, in" + filePath +
                    ", exception row number is :" + lineCount + ", Now totalCount is " + totalCount + "\r\n");
            bufferedWriter.close();
            e.printStackTrace();
        }
        finally {
            System.out.println("totalCount is :" + totalCount);
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
