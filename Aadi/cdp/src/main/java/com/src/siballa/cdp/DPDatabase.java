package com.src.siballa.cdp;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by siballa on 29/05/16.
 */
public class DPDatabase {

    public static final long ONE_DAY = 24 * 3600 * 1000;
    List<DPData> dpDB;


    DPDatabase() {
        dpDB = new ArrayList<DPData>();
    }

    /**
     * @param dt
     * @return
     */
    HashMap<String, String> getDPData(Date dt) {
        HashMap hm = null;
        //LinkedHashMap<String, String> hmOrdered;
        boolean found = false;

        for (DPData d : dpDB) {
            if (d.getDate().equals(dt))
                return d.getHm();
        }

        DPData d = new DPData(dt);
        dpDB.add(d);

        Log.d("BSN", "Added date: " + dt.toString());
        Log.d("BSN", "Log size is " + dpDB.size());
        return d.getHm();
    }
}
