package txttotable;

import java.io.*;

public class TransformToFile {

    public static void main(String[] args) throws IOException {

        String filePath = "E:\\datawarehouse\\movies.txt";
        int lineCount = 0;
        int totalCount = 0;
        int resultCount = 0;

        String txtPath = filePath;
        File filename = new File(txtPath); // 读取以上路径的input文件
        InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // 建立一个输入流对象reader
        BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言

        String splitor = ""+'\001';
        
        String line = "";
        String[] ss = null;
        MovieReview movieReview = new MovieReview();
        int batchCount = 0;

        File file = new File("review.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fileWriter = new FileWriter(file.getName(), true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        while (!line.contains("product/productId: ")) {
            line = br.readLine();
            lineCount++;
        }
        Boolean flag = true;

        while (true) {

            if (flag) {
                flag = false;
            } else {
                line = br.readLine();
                lineCount++;
            }

            if (line == null)
                break;
            else if (movieReview.getFinished() == 8) {
                //提交一条记录
                if (movieReview.getProductId().equals("B000R9AKKO") && movieReview.getUserId().equals("A2LMRZIYKNMIMR")
                        && movieReview.getTime().equals("1188604800")) {
                    //剔除该记录
                } else {
                    bufferedWriter.write(movieReview.getProductId() + splitor +
                            movieReview.getUserId() + splitor +
                            movieReview.getProfileName() + splitor +
                            movieReview.getHelpfulness() + splitor +
                            movieReview.getScore() + splitor +
                            movieReview.getTime() + splitor +
                            movieReview.getSummary() + splitor +
                            movieReview.getText() + "\r\n");

                    batchCount++;

                    if (batchCount % 5000 == 0) {
                        System.out.println("Now totalCount :" + batchCount);
                    }
                }

                //重置finished
                movieReview.resetFinished();
            } else if (line.contains("product/productId: ")) {
                ss = line.split("productId:");
                movieReview.setProductId(ss[1].trim());
                movieReview.increaseFinished();

            } else if (line.contains("review/userId: ")) {
                ss = line.split("userId:");
                movieReview.setUserId(ss[1].trim());
                movieReview.increaseFinished();

            } else if (line.contains("review/profileName: ")) {
                ss = line.split("profileName:");
                movieReview.setProfileName(ss[1].trim());
                movieReview.increaseFinished();

            } else if (line.contains("review/helpfulness: ")) {
                ss = line.split("helpfulness:");
                movieReview.setHelpfulness(ss[1].trim());
                movieReview.increaseFinished();

            } else if (line.contains("review/score: ")) {
                ss = line.split("score:");
                movieReview.setScore(Float.parseFloat(ss[1].trim()));
                movieReview.increaseFinished();

            } else if (line.contains("review/time: ")) {
                ss = line.split("time:");
                movieReview.setTime(ss[1].trim());
                movieReview.increaseFinished();

            } else if (line.contains("review/summary: ")) {
                ss = line.split("summary:");
                movieReview.setSummary(ss[1].trim());
                movieReview.increaseFinished();

            } else if (line.contains("review/text: ")) {
                ss = line.split("text:");
                movieReview.setText(ss[1].trim());
                movieReview.increaseFinished();
            }
        }

        if (movieReview.getFinished() == 8) {
            //提交一条记录
            if (movieReview.getProductId().equals("B000R9AKKO") && movieReview.getUserId().equals("A2LMRZIYKNMIMR")
                    && movieReview.getTime().equals("1188604800")) {
                //剔除该记录
            } else {
                bufferedWriter.write(movieReview.getProductId() + splitor +
                        movieReview.getUserId() + splitor +
                        movieReview.getProfileName() + splitor +
                        movieReview.getHelpfulness() + splitor +
                        movieReview.getScore() + splitor +
                        movieReview.getTime() + splitor +
                        movieReview.getSummary() + splitor +
                        movieReview.getText() + "\r\n");

                batchCount++;
            }
        }


        System.out.println(batchCount);

        bufferedWriter.close();

    }

}
