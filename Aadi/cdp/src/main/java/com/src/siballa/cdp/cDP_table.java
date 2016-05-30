package com.src.siballa.cdp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class cDP_table extends AppCompatActivity {

    ListAdapter listAdapter;
    DPDatabase dpDB;
    Date currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c_dp_table);

        dpDB = new DPDatabase();
        currentDate = new Date();
        listAdapter = new ListAdapter(this, dpDB.getDPData(currentDate));

        Button b = (Button) findViewById(R.id.pickDate);
        SimpleDateFormat ft = new SimpleDateFormat("E dd MMM yyyy");
        b.setText(ft.format(currentDate));

        ListView listView = (ListView) findViewById(R.id.dpView);
        listView.setAdapter(listAdapter);
    }

    public void dateSelect(View view) {
        switch (view.getId()) {
            case R.id.prevDate:
                currentDate = new Date(currentDate.getTime() - DPDatabase.ONE_DAY);
                break;
            case R.id.nextDate:
                currentDate = new Date(currentDate.getTime() + DPDatabase.ONE_DAY);
                break;
            case R.id.pickDate:
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
                break;
            default:
                currentDate = new Date();
        }
        listAdapter.setHm(dpDB.getDPData(currentDate));

        Button b = (Button) findViewById(R.id.pickDate);
        SimpleDateFormat ft = new SimpleDateFormat("E dd MMM yyyy");
        b.setText(ft.format(currentDate));

        Log.d("BSN", "Currnet date" + ft.format(currentDate));
    }

    private int getID(Object key) {
        return 0;
    }

    private void setDate(Date d) {
        currentDate = d;
        Button b = (Button) findViewById(R.id.pickDate);
        SimpleDateFormat ft = new SimpleDateFormat("E dd MMM yyyy");
        b.setText(ft.format(currentDate));
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

        }
    }

    private class ListAdapter extends BaseAdapter {
        Context context;
        ArrayList<String> keyList;
        ArrayList<String> valList;

        public ListAdapter(cDP_table cDP_table, HashMap<String, String> hm) {
            this.context = cDP_table;
            setHm(hm);
        }

        public void setHm(HashMap<String, String> hm) {
            if (hm == null) {
                return;
            }
            keyList = new ArrayList<String>();
            valList = new ArrayList<String>();
            Iterator it = hm.entrySet().iterator();
            while (it.hasNext()) {
                HashMap.Entry pair = (HashMap.Entry) it.next();
                int pos = getID(pair.getKey());

                if (pos == 0) {
                    keyList.add((String) pair.getKey());
                    valList.add((String) pair.getValue());
                }
            }
            this.notifyDataSetChanged();
            Log.d("BSN", "size is" + keyList.size());
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
                convertView.setBackgroundColor(Color.GRAY);
            else
                convertView.setBackgroundColor(Color.WHITE);

            Log.d("BSN", "posView: " + pos);

            return convertView;
        }
    }
}
