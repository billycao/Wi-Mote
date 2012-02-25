package com.because.wimote;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;

public class Keyboard_Activity extends Activity {
	public static final String PREFS_NAME = "WiMotePrefsFile";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Keyboard_Util.initVirtualKeyboard(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		Keyboard_Util.resetInit();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String hostname = settings.getString("hostname", "192.168.1.101");
		
		Keyboard_Util.processKeys(keyCode, event.getUnicodeChar(), Keyboard_Util.KEY_DOWN, hostname);
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String hostname = settings.getString("hostname", "192.168.1.101");
		
		Keyboard_Util.processKeys(keyCode, event.getUnicodeChar(), Keyboard_Util.KEY_UP, hostname);
		return super.onKeyUp(keyCode, event);
	}
}
