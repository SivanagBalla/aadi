package com.src.siballa.cdp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by siballa on 29/05/16.
 */
public class DPData implements Comparator<DPData>, Comparable<DPData> {

    public static final String BANGALORE = "10645";
    public static int instanceCount = 0;

    private Date date;
    private HashMap<String, String> hm;

    /**
     * @param date
     */
    public DPData(Date date) {
        this.date = date;
        hm = new HashMap<String, String>();
        SimpleDateFormat ft = new SimpleDateFormat("E dd MMM yyyy");
        String dateStr = ft.format(date);
        hm.put("Date", dateStr);
        hm.put("Date1", dateStr);
        hm.put("Date2", dateStr);
        //updateSoup(BANGALORE);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public HashMap<String, String> getHm() {
        return hm;
    }

    @Override
    public int compareTo(DPData o) {
        return this.date.compareTo(o.date);
    }

    @Override
    public int compare(DPData o1, DPData o2) {
        return o1.date.compareTo(o2.date);
    }

    public void put(String key, String val) {
        hm.put(key, val);
    }

    /**
     * @param locId
     * @return boolean
     */
    public boolean updateSoup(String locId) {

        /* Form the URL */
        SimpleDateFormat dateFmt = new SimpleDateFormat("dd/MM/yyyy");
        String dpURL = "http://www.drikpanchang.com/panchang/day-panchang.html?l="
                + locId + "&&date=" + dateFmt.format(this.date);

        /* Fetch and Parse URL */
        org.jsoup.nodes.Document doc;
        try {
            doc = Jsoup.connect(dpURL).get();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        /* Select the table with class "panchTbl" */
        Element table = doc.select("table[class=panchTbl]").first();
        if (table == null) {
            System.out.println("Null Table");
            return false;
        }

        Elements rows = table.select("tr");

        hm = new HashMap<String, String>();
        /* Parse through each row extracting required elements and adding them
         * to hashmap
         */
        for (int i = 0; i < rows.size(); i++) {
            Element row = rows.get(i);
            Elements cols = row.select("td");

            if (cols.size() == 1 &&
                    cols.get(0).text().equals("Chandrabalam and Tarabalam"))
                break;

            if (cols.size() >= 2) {
                /* Stripout ':' and '&nbsp' characters */
                String key = cols.get(0).text().replaceAll("[:\u00a0]", "");
                String val = cols.get(1).text().replace("\u00a0", "");

                if (!key.isEmpty() && !val.isEmpty()) {
                    System.out.println(key + "\t: " + val);
                    this.hm.put(key, val);
                }
            }

            if (cols.size() >= 4) {
                String key = cols.get(2).text().replaceAll("[:\u00a0]", "");
                String val = cols.get(3).text().replace("\u00a0", "");

                if (!key.isEmpty() && !val.isEmpty()) {
                    System.out.println(key + "\t: " + val);
                    this.hm.put(key, val);
                }
            }
        } /* for each row */

        return true;
    }
//
//    private static String getSampleSource(String filename) {
//        BufferedReader reader = null;
//        StringBuilder stringBuilder = new StringBuilder();
//
//        try {
//            reader = new BufferedReader(new FileReader(filename));
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        String line = null;
//
//        try {
//            while ((line = reader.readLine()) != null) {
//                stringBuilder.append(line);
//            }
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        try {
//            reader.close();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//
//        }
//        return stringBuilder.toString();
//    }

}
