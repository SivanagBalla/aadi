package com.src.siballa.cdp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by siballa on 29/05/16.
 */
public class DPDatabase implements Serializable {

    public static final long ONE_DAY = 24 * 3600 * 1000;
    List<DPData> dpDB;

    DPDatabase() {
        dpDB = new ArrayList<DPData>();
    }

    public List<DPData> getDpDB() {
        return dpDB;
    }

    /**
     * @param dt
     * @return
     */
    HashMap<String, String> getDPData(Date dt) {

        for (DPData d : dpDB) {
            if (d.getDate().equals(dt))
                return d.getHm();
        }

        DPData d = new DPData(dt);
        dpDB.add(d);

        return d.getHm();
    }

    boolean updateDPData(Date dt) {
        for (DPData d : dpDB) {
            if (d.getDate().equals(dt)) {
                return d.updateSoup(DPData.BANGALORE, false);
            }
        }
        DPData d = new DPData(dt);
        dpDB.add(d);
        return d.updateSoup(DPData.BANGALORE, true);
    }

    boolean cleanDB(){

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 6);
        c.clear(Calendar.AM_PM);
        c.clear(Calendar.MINUTE);
        c.clear(Calendar.SECOND);
        c.clear(Calendar.MILLISECOND);

        c.add(Calendar.DATE, -10);
        long startDate = c.getTime().getTime();

        Collections.sort(dpDB);

        for(int i=-10, j=0; i<60; i++) {
            long calDate = c.getTime().getTime();

            DPData d = dpDB.get(j);

            if (calDate == d.getDate().getTime()) {
                j++;
            } else if (calDate < d.getDate().getTime()) {
                dpDB.add(j, new DPData(c.getTime()));
            } else {
                System.out.println("This shouldn't happen");
            }
            c.add(Calendar.DATE, 1);
        }

        long endDate = c.getTime().getTime();
        for (DPData d : dpDB)
            if (d.getDate().getTime() < startDate || d.getDate().getTime() > endDate)
                dpDB.remove(d);

        return true;
    }

    public void updateDB() {
        cleanDB();

        updateDBTask newTask = new updateDBTask();
        newTask.execute();
    }

    private class updateDBTask extends AsyncTask<Void, Void, Boolean> {

        ProgressDialog progressDialog;
        Date prorityDate;
        boolean updateAll;


        protected Boolean doInBackground(Void ...v) {
            for (DPData d : dpDB) {
                d.updateSoup(DPData.BANGALORE, false);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (!result.booleanValue())
                System.out.println("Database updated");
        }
    }
}
