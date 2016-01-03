package amazonmovies;

import com.mysql.jdbc.MysqlDataTruncation;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Crawler implements Runnable{

    private int threadNumber = 1;
    private static Queue<String> productIds = new ConcurrentLinkedQueue<String>();
    private static PreparedStatement insertStatement;
    private static PreparedStatement updateStatement;
    private int exceptionCount = 0;

    /*public amazonmovies.Crawler(Queue<String> productIds, PreparedStatement insertStatement, PreparedStatement updateStatement) {
    }*/

    public Crawler() {}

    public static Queue<String> getProductIds() {
        return productIds;
    }

    public static void setProductIds(Queue<String> productIds) {
        Crawler.productIds = productIds;
    }

    public static PreparedStatement getInsertStatement() {
        return insertStatement;
    }

    public static void setInsertStatement(PreparedStatement insertStatement) {
        Crawler.insertStatement = insertStatement;
    }

    public static PreparedStatement getUpdateStatement() {
        return updateStatement;
    }

    public static void setUpdateStatement(PreparedStatement updateStatement) {
        Crawler.updateStatement = updateStatement;
    }

    public static Crawler create(Queue<String> productIds, PreparedStatement insertStatement, PreparedStatement updateStatement) {
        Crawler crawler = new Crawler();
        setProductIds(productIds);
        setInsertStatement(insertStatement);
        setUpdateStatement(updateStatement);
        return crawler;
    }

    public Crawler thread(int threadNumber) {
        this.threadNumber = threadNumber;
        if (threadNumber <= 0) {
            this.threadNumber = 1;
        }
        return this;
    }

    //@Override
    public void run() {
        String id = "";
        String url = "";
        String name = "";
        Document doc = null;
        int rand = -1;
        /*//文本文件输出
        try {
            int size = productIds.size();
            File file = new File("size.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file.getName(), true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(size+"\r\n");
            bufferedWriter.close();
        } catch (IOException e) {}
        //*/
        while (!productIds.isEmpty()) {
            Properties prop = System.getProperties();
            rand = Math.abs(new Random().nextInt())%21;
            if (rand == 20) {
                prop.setProperty("http.proxySet", "false");
            } else {
                prop.setProperty("http.proxySet", "true");
                prop.setProperty("http.proxyHost", Main.hosts[rand]);
                prop.setProperty("http.proxyPort", Main.ports[rand]);
            }
            //synchronized (productIds) {
                id = productIds.poll();
            //}
            url = "http://www.amazon.com/dp/" + id;
            try {
                Connection.Response response = Jsoup.connect(url)
                        .header("Connection", "Keep-Alive")
                        .header("Accept", "text/html,application/xhtml+xml,applicati" +
                                "on/xml;q=0.9,image/webp,*/*;q=0.8")
                        .header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6")
                        .header("Cache-Control", "max-age=0")
                        .header("Host", "www.amazon.com")
                        .referrer("http://snap.stanford.edu/data/web-Movies.html")
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/5" +
                                "37.36 (KHTML, like Gecko) Chrome/45.0.2454.93 Safari/537.36")
                        .followRedirects(true)
                        .timeout(30000)
                        .execute();
                doc = response.parse();
                if (String.valueOf(doc).contains("aiv-content-title")) {
                    File file1 = new File("E:\\datawarehouse\\HTMLs\\" + id + ".html");
                    if (file1.exists())
                        file1.delete();
                    if (!file1.exists()) {
                        file1.createNewFile();
                    }
                    FileWriter fileWriter = new FileWriter(file1, true);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(String.valueOf(doc));
                    bufferedWriter.close();
                    /*name = doc.getElementById("aiv-content-title").text();
                    insertStatement.setString(1, name);
                    insertStatement.execute();*/
                    updateStatement.setString(1, id);
                    updateStatement.execute();
                }
                else if (String.valueOf(doc).contains("productTitle")) {
                    File file2 = new File("E:\\datawarehouse\\HTMLs\\" + id + ".html");
                    if (file2.exists())
                        file2.delete();
                    if (!file2.exists()) {
                        file2.createNewFile();
                    }
                    FileWriter fileWriter = new FileWriter(file2, true);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(String.valueOf(doc));
                    bufferedWriter.close();
                    /*name = doc.getElementById("productTitle").text();
                    insertStatement.setString(1, name);
                    insertStatement.execute();*/
                    updateStatement.setString(1, id);
                    updateStatement.execute();
                }
                else if (String.valueOf(doc).contains("Robot Check")) {
                    productIds.add(id);
                    /*if (rand == 20) {
                        System.out.println("Robot >> localhost");
                    } else {
                        System.out.println("Robot >> host:" + amazonmovies.Main.hosts[rand] + "port:" + amazonmovies.Main.ports[rand]);
                    }*/
                    //Thread.sleep(50000);
                    int rand2 = Math.abs(new Random().nextInt())%20;
                    prop.setProperty("http.proxySet", "true");
                    prop.setProperty("http.proxyHost", Main.hosts[rand2]);
                    prop.setProperty("http.proxyPort", Main.ports[rand2]);
                }
                else {
                    try {
                        File file = new File("otherDoc.txt");
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        FileWriter fileWriter = new FileWriter(file.getName(), true);
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                        bufferedWriter.write(String.valueOf(doc)+"\r\n=====================================================================\r\n");
                        bufferedWriter.close();
                    } catch (IOException e2) {}
                    //
                }
                /*//文本文件输出
                try {
                    File file = new File("name.txt");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileWriter fileWriter = new FileWriter(file.getName(), true);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(name+"\r\n");
                    bufferedWriter.close();
                } catch (IOException e) {}
                //*/

            } catch (SocketException e) {
                productIds.add(id);
                //文本文件输出
                try {
                    File file = new File("SocketException.txt");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileWriter fileWriter = new FileWriter(file.getName(), true);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    if (rand >= 14) {
                        bufferedWriter.write("localhost"+"\r\n");
                    } else {
                        bufferedWriter.write("host:"+Main.hosts[rand]+"port:"+Main.ports[rand]+"\r\n");
                        bufferedWriter.close();
                    }
                } catch (IOException e1) {}
                //
            } catch (HttpStatusException he) {
                int statusCode = he.getStatusCode();
                switch (statusCode) {
                    case 404 : {
                        try {
                            updateStatement.setString(1, id);
                            updateStatement.execute();
                        } catch (SQLException e) {
                            //ignore
                        }
                    }
                    case 503 : {
                        //文本文件输出
                        productIds.add(id);
                        /*try {
                            File file = new File("503.txt");
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            FileWriter fileWriter = new FileWriter(file.getName(), true);
                            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                            bufferedWriter.write("503"+"\r\n");
                            bufferedWriter.close();
                        } catch (IOException e) {}*/
                        //
                    }
                }
            } catch (MySQLIntegrityConstraintViolationException e) {
                try {
                    updateStatement.setString(1, id);
                    updateStatement.execute();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    System.out.println("重复时插入出错");
                }
                /*//文本文件输出
                try {
                    File file = new File("sql-exception.txt");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileWriter fileWriter = new FileWriter(file.getName(), true);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write("{"+id+"}>>>"+e.getMessage()+"\r\n=====================================================================\r\n");
                    bufferedWriter.close();
                } catch (IOException e2) {}
                //*/
            } catch (MysqlDataTruncation e) {
                //文本文件输出
                try {
                    File file = new File("Data-truncation.txt");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileWriter fileWriter = new FileWriter(file.getName(), true);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(id+">>:"+name+"\r\n");
                    bufferedWriter.close();
                } catch (IOException e1) {}
                //
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                productIds.add(id);
                //e.printStackTrace();
            } catch (NullPointerException e) {
                //文本文件输出
                try {
                    File file = new File("otherNULL-id.txt");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileWriter fileWriter = new FileWriter(file.getName(), true);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(id+"\r\n");
                    bufferedWriter.close();
                } catch (IOException e1) {}
                //
            } catch (Exception e) {
                //productIds.add(id);
                e.printStackTrace();
            }
        }
    }

    public void start() {
        for (int i = 0; i < threadNumber; i++) {
            new Thread(this).start();
        }
    }

}
