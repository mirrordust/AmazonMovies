package parsehtml;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyTime {

    public MyTime() {
    }

    public Date formatTime(String time) {
        String[] ss = time.split(",");
        String year = ss[1].trim();
        String[] ss2 = ss[0].split(" ");
        String day = ss2[1].trim();
        String month = "";
        ss2[0] = ss2[0].trim();
        if (ss2[0].contains("Jan")) {
            month = "01";
        } else if (ss2[0].contains("Feb")) {
            month = "02";
        } else if (ss2[0].contains("Mar")) {
            month = "03";
        } else if (ss2[0].contains("Apr")) {
            month = "04";
        } else if (ss2[0].contains("May")) {
            month = "05";
        } else if (ss2[0].contains("Jun")) {
            month = "06";
        } else if (ss2[0].contains("Jul")) {
            month = "07";
        } else if (ss2[0].contains("Aug")) {
            month = "08";
        } else if (ss2[0].contains("Sep")) {
            month = "09";
        } else if (ss2[0].contains("Oct")) {
            month = "10";
        } else if (ss2[0].contains("Nov")) {
            month = "11";
        } else if (ss2[0].contains("Dec")) {
            month = "12";
        }
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = format.parse(year+"-"+month+"-"+day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public Date formatTime2(String time) {
        String[] ss = time.split(",");
        String year = ss[1].trim();
        String[] ss2 = ss[0].split(" ");
        String day = ss2[1].trim();
        String month = "";
        ss2[0] = ss2[0].trim();
        if (ss2[0].equals("January")) {
            month = "01";
        } else if (ss2[0].equals("February")) {
            month = "02";
        } else if (ss2[0].equals("March")) {
            month = "03";
        } else if (ss2[0].equals("April")) {
            month = "04";
        } else if (ss2[0].equals("May")) {
            month = "05";
        } else if (ss2[0].equals("June")) {
            month = "06";
        } else if (ss2[0].equals("July")) {
            month = "07";
        } else if (ss2[0].equals("August")) {
            month = "08";
        } else if (ss2[0].equals("September")) {
            month = "09";
        } else if (ss2[0].equals("October")) {
            month = "10";
        } else if (ss2[0].equals("November")) {
            month = "11";
        } else if (ss2[0].equals("December")) {
            month = "12";
        }
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = format.parse(year+"-"+month+"-"+day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static void main(String[] args) {

        //October 24, 2006

        MyTime myTime = new MyTime();
        Date date = myTime.formatTime("December 26, 2015");
        System.out.println(date);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        System.out.println(calendar.get(Calendar.YEAR)+"  "+calendar.get(Calendar.MONTH)
                +"  "+calendar.get(Calendar.DAY_OF_MONTH)
                +"  "+calendar.get(Calendar.DAY_OF_WEEK));


        String s = "123456789";
        System.out.println(s.length() + "  " + s.substring(0, 7));


        //Date date = new Date((long)1042502400 * 1000L);
        //calendar.setTime(date);
        //System.out.println(calendar.get(Calendar.YEAR));
    }

}