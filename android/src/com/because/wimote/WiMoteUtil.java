package com.because.wimote;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import com.because.wimote.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ToggleButton;

public class WiMoteUtil {
	private String hostname;
	private int pin;
	private int port;

	private Activity activity;
	private ArrayList<String> depressedKeyStreams = new ArrayList<String>();

	private float Accel_Sensitivity_Max = 10f;
	private float AccelMaximum = 10f;
	private float DetectionThreshold = 2f;
	private int MouseSensitivityPercent = 50;

	public static final int[] ACCEL_KEY_IDS = {R.id.LeftClick, R.id.RightClick};
	public static final int[] KEY_IDS = {R.id.A, R.id.Apostrophe, R.id.B, R.id.Backslash, R.id.Backspace, R.id.C, R.id.CapsLock, R.id.Comma, R.id.D, R.id.Dash, R.id.Down, R.id.E, R.id.Eight, R.id.Enter, R.id.Equals, R.id.Esc, R.id.F, R.id.Five, R.id.Four, R.id.G, R.id.Grave, R.id.H, R.id.I, R.id.J, R.id.K, R.id.L, R.id.Left, R.id.LeftClick, R.id.LeftSquareBracket, R.id.M, R.id.N, R.id.Nine, R.id.O, R.id.One, R.id.P, R.id.Period, R.id.Q, R.id.R, R.id.Right, R.id.RightClick, R.id.RightSquareBracket, R.id.S, R.id.Semicolon, R.id.Seven, R.id.Six, R.id.Slash, R.id.Space, R.id.T, R.id.Tab, R.id.Three, R.id.Two, R.id.U, R.id.Up, R.id.V, R.id.W, R.id.X, R.id.Y, R.id.Z, R.id.Zero};
	public static final String[] KEY_NAMES = {"a", "'", "b", "\\", "BACKSPACE", "c", "CAPS", ",", "d", "-", "DOWN", "e", "8", "ENTER", "=", "ESC", "f", "5", "4", "g", "`", "h", "i", "j", "k", "l", "LEFT", "MOUSE_LEFT_CLICK", "[", "m", "n", "9", "o", "1", "p", ".", "q", "r", "RIGHT", "MOUSE_RIGHT_CLICK", "]", "s", ";", "7", "6", "/", "SPACE", "t", "TAB", "3", "2", "u", "UP", "v", "w", "x", "y", "z", "0"};
	public static final int[] MODIFIER_KEY_IDS = {R.id.Alt, R.id.Ctrl, R.id.Shift, R.id.Win};
	public static final String[] MODIFIER_KEY_NAMES = {"ALT", "CTRL", "SHIFT", "WIN"};

	public static final String KEY_DOWN = "KEY_DOWN";
	public static final String KEY_UP = "KEY_UP";

	public static String getKeyName(int keyId) {
		for (int i = 0; i < KEY_IDS.length; i++)
			if (KEY_IDS[i] == keyId)
				return KEY_NAMES[i];
		return "";
	}

	public static String getModifierKeyName(int modifierKeyId) {
		for (int i = 0; i < MODIFIER_KEY_IDS.length; i++)
			if (MODIFIER_KEY_IDS[i] == modifierKeyId)
				return MODIFIER_KEY_NAMES[i];
		return "";
	}

	public void sendString(String s) {
		try {
<<<<<<< HEAD
=======
			String string = Integer.toString(pin) + " " + s;
			DatagramSocket socket = new DatagramSocket();
>>>>>>> aa1895e13bbfa4b92ed2d82aa17da200e8b5c588
			byte[] data = string.getBytes();
			DatagramSocket socket = new DatagramSocket();
			socket.connect(InetAddress.getByName(hostname), port);
			DatagramPacket packet = new DatagramPacket(data, data.length);
			socket.send(packet);
			socket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public WiMoteUtil(Activity a) {
		activity = a;
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);
		hostname = settings.getString("hostname", "192.168.1.101");
		pin = Integer.parseInt(settings.getString("pinnumber", "1337"));
		port = Integer.parseInt(settings.getString("portnumber", "27015"));	
		DetectionThreshold = Float.parseFloat(settings.getString("aThreshold", "2f"));
		MouseSensitivityPercent = settings.getInt("aMouseSensitivity", 50);
	}

	public void processKeys(String szKeyAction, String szKeyName, boolean isKeyboard) {
		if (szKeyAction.isEmpty() || szKeyName.isEmpty())
			return;

		if (szKeyName.length() == 1)
			if (szKeyName.charAt(0) < 32 || szKeyName.charAt(0) > 126)
				return;

		// By default, modifier keys (e.g. SHIFT, CTRL) act as toggle buttons.
		// This behavior can be disabled by turning off the "Mod" button. In
		// that case, modifier keys can be pressed individually just like any
		// other key, which is useful in certain applications such as games.
		String szModifierKeys = new String();
		if(isKeyboard) {
			if (((ToggleButton)activity.findViewById(R.id.ToggleModifiers)).isChecked()) {
				if (((ToggleButton)activity.findViewById(R.id.Shift)).isChecked())
					szModifierKeys += (getModifierKeyName(R.id.Shift) + " ");
				if (((ToggleButton)activity.findViewById(R.id.Ctrl)).isChecked())
					szModifierKeys += (getModifierKeyName(R.id.Ctrl) + " ");
				if (((ToggleButton)activity.findViewById(R.id.Win)).isChecked())
					szModifierKeys += (getModifierKeyName(R.id.Win) + " ");
				if (((ToggleButton)activity.findViewById(R.id.Alt)).isChecked())
					szModifierKeys += (getModifierKeyName(R.id.Alt) + " ");
			}
		}

		String szKeyStream = szModifierKeys + szKeyName;

		if (szKeyStream.isEmpty())
			return;

		if (szKeyAction.equals(KEY_DOWN)) {
			// We are asked to depress specific key(s) that is/are NOT
			// currently depressed.
			if (!depressedKeyStreams.contains(szKeyStream)) {
				sendString(KEY_DOWN + " " + szKeyStream);
				depressedKeyStreams.add(szKeyStream);
			}
		}
		else if (szKeyAction.equals(KEY_UP)) {
			// We are asked to release specific key(s) that is/are currently
			// depressed.
			if (depressedKeyStreams.contains(szKeyStream)) {
				sendString(KEY_UP + " " + szKeyStream);
				depressedKeyStreams.remove(szKeyStream);
			}
		}
	}

	public float[] processAccel(float x, float y, float z) {
		float PercentX = 0f, PercentY = 0f, PercentZ = 0f;

		//under normal operation of the accelerometer the tilting of the phone to a 90* angle will
		//correspond with values in the range of 0->10, however if the phone is shaken or moved quickly
		//the value may exceed 10, so the .min function will limit the value to 10, this is then scaled
		//up to 30, representing 30% of the screen traveled in a unit of time (TBD)

		//TODO: set once in beginning.
		float PerS = Accel_Sensitivity_Max * (float)MouseSensitivityPercent / 100f;

		//TODO: can we reduce multiplications
		//Scale and limit the Accelerometer data to a range between 0->30 (representing percent value for mouse movement)
		PercentX = Math.min(x, AccelMaximum) * PerS;
		PercentY = Math.min(y, AccelMaximum) * PerS;
		//PercentZ = Math.min(z, AccelMaximum)*Accel_Sensitivity_Max;

		PercentX = Math.max(PercentX, -AccelMaximum) * PerS;
		PercentY = Math.max(PercentY, -AccelMaximum) * PerS;
		//PercentZ = -Math.max(z, -AccelMaximum)*Accel_Sensitivity_Max;

		//to ignore noise while holding the phone still.
		if (Math.abs(PercentX) < DetectionThreshold)
			PercentX = 0;
		else // the threshold is subtracted (or added) to the Percent value to scale it back for the threshold
			PercentX = PercentX - DetectionThreshold * (PercentX / Math.abs(PercentX));

		if (Math.abs(PercentY) < DetectionThreshold)
			PercentY = 0;
		else
			PercentY = PercentY - DetectionThreshold * (PercentY / Math.abs(PercentY));

		if (Math.abs(PercentZ) < DetectionThreshold)
			PercentZ = 0;
		else
			PercentZ = PercentZ - DetectionThreshold * (PercentZ / Math.abs(PercentZ));

		return (new float[] {-PercentX, PercentY});
	}
}
