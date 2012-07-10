package com.because.wimote;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class MyPreferenceActivity extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		addPreferencesFromResource(R.xml.preferences_accel);
		addPreferencesFromResource(R.xml.preferences_keyboard);
		addPreferencesFromResource(R.xml.preferences_trackpad);
	}
}
