package com.src.siballa.cdp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class cDP_table extends AppCompatActivity {

    final SimpleDateFormat dateFmtr = new SimpleDateFormat("E dd MMM yyyy");
    final String FILENAME = "dpDBFile";

    final String[][] str = {
            {"Hindu Sunrise", "Sunrise"},
            {"Hindu Sunset", "Sunset"},
            {"Moonrise", ""},
            {"Moonset", ""},
            {"Shaka Samvat", ""},
            {"Chandramasa", ""},
            {"Paksha", ""},
            {"Ththi", ""},
            {"Skipped Ththi", ""},
            {"Nakshatra", ""},
            {"Skipped Nakshatra", ""},
            {"Yoga", ""},
            {"Skipped Yoga", ""},
            {"First Karana", ""},
            {"Second Karana", ""},
            {"Rahu Kalam", ""},
            {"Dur Muhurtam", ""},
            {"Yamaganda", ""},
            {"Varjyam", ""},
            {"Gulikai Kalam", ""},
            {"Abhijit Muhurta", ""},
            {"Amrit kalam", ""},
            {"Drik Ayana", ""},
            {"Drik Ritu", ""},
    };
    ListAdapter dpListAdapter;
    DPDatabase dpDB;
    Calendar calendar;
    DatePickerFragment datePickerFrag;
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year,
                              int month, int day) {
            calendar.set(year, month, day, 0, 0);
            updateDate();
            return;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c_dp_table);

        String string = "hello world!";

        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = openFileInput(FILENAME);
            ois = new ObjectInputStream(fis);
            dpDB = (DPDatabase) ois.readObject();
            System.out.println("onCreate loaded" + dpDB.getDpDB().size());
        } catch (Exception e) {
            System.out.println(e);
            dpDB = new DPDatabase();
        } finally {
            try {
                if (ois != null)
                    ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        dpDB.updateDB();

        calendar = Calendar.getInstance();
        dpListAdapter = new ListAdapter(this);

        updateDate();

        ListView dpListView = (ListView) findViewById(R.id.dpView);
        dpListView.setAdapter(dpListAdapter);
    }

    protected void onPause() {
        super.onPause();

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        System.out.println("onPaused ");

        try {
            fos = openFileOutput(FILENAME, MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(dpDB);
            System.out.println("Data Saved " + dpDB.getDpDB().size());
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                if (oos != null)
                    oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void dateSelect(View view) {
        switch (view.getId()) {
            case R.id.prevDate:
                calendar.add(Calendar.DATE, -1);
                updateDate();
                break;
            case R.id.nextDate:
                calendar.add(Calendar.DATE, 1);
                updateDate();
                break;
            case R.id.pickDate:
                if (datePickerFrag == null) {
                    datePickerFrag = new DatePickerFragment();
                    datePickerFrag.setCallBack(datePickerListener);
                }

                Bundle bundle = new Bundle();
                bundle.putInt("day", calendar.get(Calendar.DATE));
                bundle.putInt("month", calendar.get(Calendar.MONTH));
                bundle.putInt("year", calendar.get(Calendar.YEAR));

                datePickerFrag.setArguments(bundle);
                datePickerFrag.show(getSupportFragmentManager(), "Pick a Date");
                break;
            default:
                break;
        }
    }

    private void updateDate() {

        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.clear(Calendar.AM_PM);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);

        Button datePicketBtn = (Button) findViewById(R.id.pickDate);
        datePicketBtn.setText(dateFmtr.format(calendar.getTime()));

        getDPDataTask newTask = new getDPDataTask();
        newTask.execute(calendar);

    }

    private int getID(String key) {
        for (int i = 0; i < str.length; i++)
            if (str[i][0].equals(key))
                return i;
        return 0;
    }


    public static class DatePickerFragment extends DialogFragment {
        DatePickerDialog.OnDateSetListener onDateSetListener;
        int year, month, day;

        public DatePickerFragment() {
        }

        @Override
        public void setArguments(Bundle bundle) {
            year = bundle.getInt("year");
            month = bundle.getInt("month");
            day = bundle.getInt("day");
        }

        public void setCallBack(DatePickerDialog.OnDateSetListener ondate) {
            onDateSetListener = ondate;
        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new DatePickerDialog(getActivity(), onDateSetListener, year, month, day);
        }
    }

    private class getDPDataTask extends AsyncTask<Calendar, Void, Boolean> {

        ProgressDialog progressDialog;
        Date prorityDate;
        boolean updateAll;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(cDP_table.this);
            progressDialog.setMessage("Fetching data");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected Boolean doInBackground(Calendar... dates) {
            if (dates.length != 0 )
                return dpDB.updateDPData(dates[0].getTime());
            return false;
        }

        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if (!result.booleanValue())
                Toast.makeText(getApplicationContext(), "Failed to fetch", Toast.LENGTH_SHORT).show();
            dpListAdapter.setHm(dpDB.getDPData(calendar.getTime()));
        }
    }

    private class ListAdapter extends BaseAdapter {


        Context context;
        ArrayList<String> keyList;
        ArrayList<String> valList;

        public ListAdapter(cDP_table cDP_table) {
            this.context = cDP_table;
            keyList = new ArrayList<String>();
            valList = new ArrayList<String>();
        }

        public void setHm(HashMap<String, String> hm) {
            if (hm == null) {
                return;
            }
            keyList = new ArrayList<String>(hm.size());
            valList = new ArrayList<String>(hm.size());

            for (int i = 0; i < str.length; i++) {
                String val = hm.get(str[i][0]);
                if (val != null) {
                    if (str[i][1].isEmpty())
                        keyList.add(str[i][0]);
                    else
                        keyList.add(str[i][1]);
                    valList.add(val);
                }
            }

            Iterator it = hm.entrySet().iterator();
            while (it.hasNext()) {
                HashMap.Entry pair = (HashMap.Entry) it.next();
                int pos = getID((String) pair.getKey());

                if (pos == 0) {
                    keyList.add((String) pair.getKey());
                    valList.add((String) pair.getValue());
                }
            }
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return keyList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.dp_list_layout, parent, false);
            }

            TextView keyView = (TextView) convertView.findViewById(R.id.keyTV);
            TextView valView = (TextView) convertView.findViewById(R.id.valueTV);

            keyView.setText(keyList.get(pos));
            valView.setText(valList.get(pos));

            if (pos % 2 == 0)
                convertView.setBackgroundColor(getResources().getColor(R.color.laEvenRow));
            else
                convertView.setBackgroundColor(getResources().getColor(R.color.laOddRow));
            return convertView;
        }
    }
}
