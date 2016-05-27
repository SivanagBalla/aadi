package com.src.siballa.aadi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DevAct extends AppCompatActivity {
    private static final String TAG = "BSN " + MainActivity.class.getSimpleName();
    private int cnt_tme = 0;
    private int cnt_ntme = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev);
    }
    public void tme_onClick(View view) {
        String txt = "WTF (What a Terrible Fault)";
        if( view.getId() == R.id.tme ) {

            cnt_ntme = 0;
            Log.d(TAG, "TouchMe onClick " + cnt_tme);
            switch(cnt_tme){
                case 0:
                    txt = "Touch me Again";
                    break;
                case 1:
                    txt = "and Again";
                    break;
                case 2:
                    txt = "Ok. That's enough";
                    break;
                case 3:
                    txt = "That's enough touching. Stop now";
                    break;
                case 4:
                    txt = "I said STOP";
                    break;
                case 5:
                    txt = "Get Lost";
                    break;
                case 6:
                    txt = "Bye Bye";
                    this.finish();
                    System.exit(0);
                    break;
            }
            ((TextView)findViewById(R.id.stv)).setText(txt);
            cnt_tme++;
        } else if(view.getId() == R.id.nTme ) {
            cnt_tme = 0;
            Log.d(TAG, "TouchMeNot onClick " + cnt_ntme );
            switch(cnt_ntme){
                case 0:
                    txt = "Told you not to touch me";
                    break;
                case 1:
                    txt = "I'm Serious";
                    break;
                case 2:
                    txt = "@$@WT#$#FF$#^";
                    break;
                case 3:
                    txt = "Touch now";
                    ((Button)findViewById(R.id.nTme)).setText("I'm an Idiot");
                    break;
                default:
                    txt = "Oh Yes, you are an Idiot";
                    break;
            }
            cnt_ntme++;
            ((TextView)findViewById(R.id.stv)).setText(txt);
        }
    }
}
