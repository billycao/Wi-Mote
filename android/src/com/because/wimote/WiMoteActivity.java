package com.because.wimote;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class WiMoteActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.main);
        
        
        TextView tv = new TextView(this);
        tv.setText("I am the WiMote Rawr!");
        setContentView(tv);
    }
}