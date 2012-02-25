package com.because.wimote;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preference_Activity extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
	}
}
