package com.because.wimote;

import android.app.Activity;
import android.content.Intent;
//import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class WiMote_Activity extends Activity {
	public static final String PREFS_NAME = "WiMotePrefsFile";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Restore preferences
        //SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    }
    
    /** Main menu */
    public void button_startAccel(View view) {
    	Intent intent = new Intent(view.getContext(), Accel_Activity.class);
    	startActivity(intent);
    }
    public void button_startTrackpad(View view) {
    	Intent intent = new Intent(view.getContext(), Trackpad_Activity.class);
    	startActivity(intent);
    }
    public void button_startKeyboard(View view) {
    	Intent intent = new Intent(view.getContext(), Keyboard_Activity.class);
    	startActivity(intent);
    }
    public void button_startSettings(View view) {
		Intent intent = new Intent(view.getContext(), Preference_Activity.class);
		startActivity(intent);
    }
    
    /** Options menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main_menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case R.id.menu_settings:
    			// Open preferences
    			Intent intent = new Intent(getBaseContext(), Preference_Activity.class);
    			startActivity(intent);
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
}