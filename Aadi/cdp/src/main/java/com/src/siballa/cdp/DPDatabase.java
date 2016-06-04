package com.src.siballa.cdp;

import java.io.Serializable;
import java.util.ArrayList;
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
}
