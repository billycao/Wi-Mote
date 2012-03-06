package com.because.wimote;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

public class Keyboard_Activity extends Activity {
	private WiMoteUtil util;

	private final int KEYCODE_CAPS_LOCK = 115;
	private final int KEYCODE_ESCAPE = 111;

	private OnClickListener modifierClickListener = new OnClickListener() {
		public void onClick(View v) {
			if (!((ToggleButton)findViewById(R.id.ToggleModifiers)).isChecked()) {
				((ToggleButton)findViewById(R.id.Shift)).setChecked(false);
				((ToggleButton)findViewById(R.id.Ctrl)).setChecked(false);
				((ToggleButton)findViewById(R.id.Win)).setChecked(false);
				((ToggleButton)findViewById(R.id.Alt)).setChecked(false);
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.keyboard);
		((ToggleButton)findViewById(R.id.Shift)).setOnClickListener(modifierClickListener);
		((ToggleButton)findViewById(R.id.Ctrl)).setOnClickListener(modifierClickListener);
		((ToggleButton)findViewById(R.id.Win)).setOnClickListener(modifierClickListener);
		((ToggleButton)findViewById(R.id.Alt)).setOnClickListener(modifierClickListener);
		((ToggleButton)findViewById(R.id.ToggleModifiers)).setOnClickListener(modifierClickListener);
		((ToggleButton)findViewById(R.id.ToggleModifiers)).setChecked(true);

		util = new WiMoteUtil(this);
	}

	private void processKeys(String szKeyAction, int keyCode, int character) {
		String szKeyName = new String();;
		switch (keyCode) {
			case KEYCODE_ESCAPE:
				szKeyName = WiMoteUtil.getKeyName(R.id.Esc);
				break;
			case KeyEvent.KEYCODE_DEL:
				szKeyName = WiMoteUtil.getKeyName(R.id.Backspace);
				break;
			case KeyEvent.KEYCODE_TAB:
				szKeyName = WiMoteUtil.getKeyName(R.id.Tab);
				break;
			case KEYCODE_CAPS_LOCK:
				szKeyName = WiMoteUtil.getKeyName(R.id.CapsLock);
				break;
			case KeyEvent.KEYCODE_ENTER:
				szKeyName = WiMoteUtil.getKeyName(R.id.Enter);
				break;
			case KeyEvent.KEYCODE_SPACE:
				szKeyName = WiMoteUtil.getKeyName(R.id.Space);
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				szKeyName = WiMoteUtil.getKeyName(R.id.Up);
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				szKeyName = WiMoteUtil.getKeyName(R.id.Down);
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				szKeyName = WiMoteUtil.getKeyName(R.id.Left);
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				szKeyName = WiMoteUtil.getKeyName(R.id.Right);
				break;
			default:
				if (character < 32 || character > 126)
					return;
				szKeyName = Character.toString((char)character);
				break;
		}
		util.processKeys(szKeyAction, szKeyName, true);
	}

	// These are for built-in Android soft keyboard or physical keyboard.
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		processKeys(WiMoteUtil.KEY_DOWN, keyCode, event.getUnicodeChar());
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		processKeys(WiMoteUtil.KEY_UP, keyCode, event.getUnicodeChar());
		return super.onKeyUp(keyCode, event);
	}
}
