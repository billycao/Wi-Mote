package com.because.wimote;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

public class Keyboard_Util extends Activity{
	// Eventually, we will provide a way for the user to enter the hostname.
	private static final int port = 27015;

	private static Activity activity;
	private static boolean bInited = false;
	private static boolean bInited2 = false;
	private static ArrayList<String> keyStreamsDown = new ArrayList<String>();		// List of keys currently depressed.
	private static boolean bLeftMouseButtonDown = false;
	private static boolean bMiddleMouseButtonDown = false;
	private static boolean bRightMouseButtonDown = false;

	public static final int KEY_DOWN = 0;
	public static final int KEY_UP = 1;
	public static final int MOUSE_LEFT_DOWN = 2;
	public static final int MOUSE_LEFT_UP = 3;
	public static final int MOUSE_MIDDLE_DOWN = 4;
	public static final int MOUSE_MIDDLE_UP = 5;
	public static final int MOUSE_RIGHT_DOWN = 6;
	public static final int MOUSE_RIGHT_UP = 7;

	public static final int KEYCODE_CAPS_LOCK = 115;
	public static final int KEYCODE_CTRL_LEFT = 113;
	public static final int KEYCODE_ESCAPE = 111;
	public static final int KEYCODE_META_LEFT = 117;

	public static Button buttonGrave;
	public static Button buttonOne;
	public static Button buttonTwo;
	public static Button buttonThree;
	public static Button buttonFour;
	public static Button buttonFive;
	public static Button buttonSix;
	public static Button buttonSeven;
	public static Button buttonEight;
	public static Button buttonNine;
	public static Button buttonZero;
	public static Button buttonDash;
	public static Button buttonEquals;
	public static Button buttonBackspace;
	public static Button buttonTab;
	public static Button buttonQ;
	public static Button buttonW;
	public static Button buttonE;
	public static Button buttonR;
	public static Button buttonT;
	public static Button buttonY;
	public static Button buttonU;
	public static Button buttonI;
	public static Button buttonO;
	public static Button buttonP;
	public static Button buttonLeftSquareBracket;
	public static Button buttonRightSquareBracket;
	public static Button buttonBackslash;
	public static Button buttonCapsLock;
	public static Button buttonA;
	public static Button buttonS;
	public static Button buttonD;
	public static Button buttonF;
	public static Button buttonG;
	public static Button buttonH;
	public static Button buttonJ;
	public static Button buttonK;
	public static Button buttonL;
	public static Button buttonSemicolon;
	public static Button buttonApostrophe;
	public static Button buttonEnter;
	public static ToggleButton toggleButtonShift;
	public static Button buttonZ;
	public static Button buttonX;
	public static Button buttonC;
	public static Button buttonV;
	public static Button buttonB;
	public static Button buttonN;
	public static Button buttonM;
	public static Button buttonComma;
	public static Button buttonPeriod;
	public static Button buttonSlash;
	public static Button buttonLeftClick;
	public static Button buttonRightClick;
	public static ToggleButton toggleButtonCtrl;
	public static ToggleButton toggleButtonWin;
	public static ToggleButton toggleButtonAlt;
	public static Button buttonEsc;
	public static Button buttonSpace;
	public static Button buttonLeft;
	public static Button buttonUp;
	public static Button buttonDown;
	public static Button buttonRight;
	public static ToggleButton toggleButtonToggleModifiers;

	public static Rect rectGrave;
	public static Rect rectOne;
	public static Rect rectTwo;
	public static Rect rectThree;
	public static Rect rectFour;
	public static Rect rectFive;
	public static Rect rectSix;
	public static Rect rectSeven;
	public static Rect rectEight;
	public static Rect rectNine;
	public static Rect rectZero;
	public static Rect rectDash;
	public static Rect rectEquals;
	public static Rect rectBackspace;
	public static Rect rectTab;
	public static Rect rectQ;
	public static Rect rectW;
	public static Rect rectE;
	public static Rect rectR;
	public static Rect rectT;
	public static Rect rectY;
	public static Rect rectU;
	public static Rect rectI;
	public static Rect rectO;
	public static Rect rectP;
	public static Rect rectLeftSquareBracket;
	public static Rect rectRightSquareBracket;
	public static Rect rectBackslash;
	public static Rect rectCapsLock;
	public static Rect rectA;
	public static Rect rectS;
	public static Rect rectD;
	public static Rect rectF;
	public static Rect rectG;
	public static Rect rectH;
	public static Rect rectJ;
	public static Rect rectK;
	public static Rect rectL;
	public static Rect rectSemicolon;
	public static Rect rectApostrophe;
	public static Rect rectEnter;
	public static Rect rectShift;
	public static Rect rectZ;
	public static Rect rectX;
	public static Rect rectC;
	public static Rect rectV;
	public static Rect rectB;
	public static Rect rectN;
	public static Rect rectM;
	public static Rect rectComma;
	public static Rect rectPeriod;
	public static Rect rectSlash;
	public static Rect rectLeftClick;
	public static Rect rectRightClick;
	public static Rect rectCtrl;
	public static Rect rectWin;
	public static Rect rectAlt;
	public static Rect rectEsc;
	public static Rect rectSpace;
	public static Rect rectLeft;
	public static Rect rectUp;
	public static Rect rectDown;
	public static Rect rectRight;

	private static Rect getViewRect(View view) {
		Rect rectParent = new Rect();
		((Keyboard_LinearLayout)activity.findViewById(R.id.Parent)).getGlobalVisibleRect(rectParent);

		Rect rect = new Rect();
		view.getGlobalVisibleRect(rect);
		rect.offset(-rectParent.left, -rectParent.top);

		return rect;
	}

	private static OnClickListener modifierClickListener = new OnClickListener() {
		public void onClick(View v) {
			if (!isModOn())
				turnOffAllModifierKeys();
		}
	};

	public static boolean isInited() {
		return bInited;
	}

	public static boolean isInited2() {
		return bInited2;
	}

	public static void resetInit() {
		bInited = false;
		bInited2 = false;
	}

	public static boolean isModOn() {
		return toggleButtonToggleModifiers.isChecked();
	}

	public static void resetVirtualKeyboard() {
		buttonGrave.setPressed(false);
		buttonOne.setPressed(false);
		buttonTwo.setPressed(false);
		buttonThree.setPressed(false);
		buttonFour.setPressed(false);
		buttonFive.setPressed(false);
		buttonSix.setPressed(false);
		buttonSeven.setPressed(false);
		buttonEight.setPressed(false);
		buttonNine.setPressed(false);
		buttonZero.setPressed(false);
		buttonDash.setPressed(false);
		buttonEquals.setPressed(false);
		buttonBackspace.setPressed(false);
		buttonTab.setPressed(false);
		buttonQ.setPressed(false);
		buttonW.setPressed(false);
		buttonE.setPressed(false);
		buttonR.setPressed(false);
		buttonT.setPressed(false);
		buttonY.setPressed(false);
		buttonU.setPressed(false);
		buttonI.setPressed(false);
		buttonO.setPressed(false);
		buttonP.setPressed(false);
		buttonLeftSquareBracket.setPressed(false);
		buttonRightSquareBracket.setPressed(false);
		buttonBackslash.setPressed(false);
		buttonCapsLock.setPressed(false);
		buttonA.setPressed(false);
		buttonS.setPressed(false);
		buttonD.setPressed(false);
		buttonF.setPressed(false);
		buttonG.setPressed(false);
		buttonH.setPressed(false);
		buttonJ.setPressed(false);
		buttonK.setPressed(false);
		buttonL.setPressed(false);
		buttonSemicolon.setPressed(false);
		buttonApostrophe.setPressed(false);
		buttonEnter.setPressed(false);
		toggleButtonShift.setPressed(false);
		buttonZ.setPressed(false);
		buttonX.setPressed(false);
		buttonC.setPressed(false);
		buttonV.setPressed(false);
		buttonB.setPressed(false);
		buttonN.setPressed(false);
		buttonM.setPressed(false);
		buttonComma.setPressed(false);
		buttonPeriod.setPressed(false);
		buttonSlash.setPressed(false);
		buttonLeftClick.setPressed(false);
		buttonRightClick.setPressed(false);
		toggleButtonCtrl.setPressed(false);
		toggleButtonWin.setPressed(false);
		toggleButtonAlt.setPressed(false);
		buttonEsc.setPressed(false);
		buttonSpace.setPressed(false);
		buttonLeft.setPressed(false);
		buttonUp.setPressed(false);
		buttonDown.setPressed(false);
		buttonRight.setPressed(false);
	}

	public static void initVirtualKeyboard(Activity a) {
		if (isInited())
			return;

		activity = a;

		activity.setContentView(R.layout.keyboard);

		buttonGrave = (Button)activity.findViewById(R.id.Grave);
		buttonOne = (Button)activity.findViewById(R.id.One);
		buttonTwo = (Button)activity.findViewById(R.id.Two);
		buttonThree = (Button)activity.findViewById(R.id.Three);
		buttonFour = (Button)activity.findViewById(R.id.Four);
		buttonFive = (Button)activity.findViewById(R.id.Five);
		buttonSix = (Button)activity.findViewById(R.id.Six);
		buttonSeven = (Button)activity.findViewById(R.id.Seven);
		buttonEight = (Button)activity.findViewById(R.id.Eight);
		buttonNine = (Button)activity.findViewById(R.id.Nine);
		buttonZero = (Button)activity.findViewById(R.id.Zero);
		buttonDash = (Button)activity.findViewById(R.id.Dash);
		buttonEquals = (Button)activity.findViewById(R.id.Equals);
		buttonBackspace = (Button)activity.findViewById(R.id.Backspace);
		buttonTab = (Button)activity.findViewById(R.id.Tab);
		buttonQ = (Button)activity.findViewById(R.id.Q);
		buttonW = (Button)activity.findViewById(R.id.W);
		buttonE = (Button)activity.findViewById(R.id.E);
		buttonR = (Button)activity.findViewById(R.id.R);
		buttonT = (Button)activity.findViewById(R.id.T);
		buttonY = (Button)activity.findViewById(R.id.Y);
		buttonU = (Button)activity.findViewById(R.id.U);
		buttonI = (Button)activity.findViewById(R.id.I);
		buttonO = (Button)activity.findViewById(R.id.O);
		buttonP = (Button)activity.findViewById(R.id.P);
		buttonLeftSquareBracket = (Button)activity.findViewById(R.id.LeftSquareBracket);
		buttonRightSquareBracket = (Button)activity.findViewById(R.id.RightSquareBracket);
		buttonBackslash = (Button)activity.findViewById(R.id.Backslash);
		buttonCapsLock = (Button)activity.findViewById(R.id.CapsLock);
		buttonA = (Button)activity.findViewById(R.id.A);
		buttonS = (Button)activity.findViewById(R.id.S);
		buttonD = (Button)activity.findViewById(R.id.D);
		buttonF = (Button)activity.findViewById(R.id.F);
		buttonG = (Button)activity.findViewById(R.id.G);
		buttonH = (Button)activity.findViewById(R.id.H);
		buttonJ = (Button)activity.findViewById(R.id.J);
		buttonK = (Button)activity.findViewById(R.id.K);
		buttonL = (Button)activity.findViewById(R.id.L);
		buttonSemicolon = (Button)activity.findViewById(R.id.Semicolon);
		buttonApostrophe = (Button)activity.findViewById(R.id.Apostrophe);
		buttonEnter = (Button)activity.findViewById(R.id.Enter);
		toggleButtonShift = (ToggleButton)activity.findViewById(R.id.Shift);
		buttonZ = (Button)activity.findViewById(R.id.Z);
		buttonX = (Button)activity.findViewById(R.id.X);
		buttonC = (Button)activity.findViewById(R.id.C);
		buttonV = (Button)activity.findViewById(R.id.V);
		buttonB = (Button)activity.findViewById(R.id.B);
		buttonN = (Button)activity.findViewById(R.id.N);
		buttonM = (Button)activity.findViewById(R.id.M);
		buttonComma = (Button)activity.findViewById(R.id.Comma);
		buttonPeriod = (Button)activity.findViewById(R.id.Period);
		buttonSlash = (Button)activity.findViewById(R.id.Slash);
		buttonLeftClick = (Button)activity.findViewById(R.id.LeftClick);
		buttonRightClick = (Button)activity.findViewById(R.id.RightClick);
		toggleButtonCtrl = (ToggleButton)activity.findViewById(R.id.Ctrl);
		toggleButtonWin = (ToggleButton)activity.findViewById(R.id.Win);
		toggleButtonAlt = (ToggleButton)activity.findViewById(R.id.Alt);
		buttonEsc = (Button)activity.findViewById(R.id.Esc);
		buttonSpace = (Button)activity.findViewById(R.id.Space);
		buttonLeft = (Button)activity.findViewById(R.id.Left);
		buttonUp = (Button)activity.findViewById(R.id.Up);
		buttonDown = (Button)activity.findViewById(R.id.Down);
		buttonRight = (Button)activity.findViewById(R.id.Right);
		toggleButtonToggleModifiers = (ToggleButton)activity.findViewById(R.id.ToggleModifiers); 

		toggleButtonShift.setOnClickListener(modifierClickListener);
		toggleButtonCtrl.setOnClickListener(modifierClickListener);
		toggleButtonWin.setOnClickListener(modifierClickListener);
		toggleButtonAlt.setOnClickListener(modifierClickListener);
		toggleButtonToggleModifiers.setOnClickListener(modifierClickListener);
		toggleButtonToggleModifiers.setChecked(true);

		resetVirtualKeyboard();

		bInited = true;
	}
	
	public static void initVirtualKeyboard2() {
		if (!isInited() || isInited2())
			return;

		rectGrave = getViewRect(buttonGrave);
		rectOne = getViewRect(buttonOne);
		rectTwo = getViewRect(buttonTwo);
		rectThree = getViewRect(buttonThree);
		rectFour = getViewRect(buttonFour);
		rectFive = getViewRect(buttonFive);
		rectSix = getViewRect(buttonSix);
		rectSeven = getViewRect(buttonSeven);
		rectEight = getViewRect(buttonEight);
		rectNine = getViewRect(buttonNine);
		rectZero = getViewRect(buttonZero);
		rectDash = getViewRect(buttonDash);
		rectEquals = getViewRect(buttonEquals);
		rectBackspace = getViewRect(buttonBackspace);
		rectTab = getViewRect(buttonTab);
		rectQ = getViewRect(buttonQ);
		rectW = getViewRect(buttonW);
		rectE = getViewRect(buttonE);
		rectR = getViewRect(buttonR);
		rectT = getViewRect(buttonT);
		rectY = getViewRect(buttonY);
		rectU = getViewRect(buttonU);
		rectI = getViewRect(buttonI);
		rectO = getViewRect(buttonO);
		rectP = getViewRect(buttonP);
		rectLeftSquareBracket = getViewRect(buttonLeftSquareBracket);
		rectRightSquareBracket = getViewRect(buttonRightSquareBracket);
		rectBackslash = getViewRect(buttonBackslash);
		rectCapsLock = getViewRect(buttonCapsLock);
		rectA = getViewRect(buttonA);
		rectS = getViewRect(buttonS);
		rectD = getViewRect(buttonD);
		rectF = getViewRect(buttonF);
		rectG = getViewRect(buttonG);
		rectH = getViewRect(buttonH);
		rectJ = getViewRect(buttonJ);
		rectK = getViewRect(buttonK);
		rectL = getViewRect(buttonL);
		rectSemicolon = getViewRect(buttonSemicolon);
		rectApostrophe = getViewRect(buttonApostrophe);
		rectEnter = getViewRect(buttonEnter);
		rectShift = getViewRect(toggleButtonShift);
		rectZ = getViewRect(buttonZ);
		rectX = getViewRect(buttonX);
		rectC = getViewRect(buttonC);
		rectV = getViewRect(buttonV);
		rectB = getViewRect(buttonB);
		rectN = getViewRect(buttonN);
		rectM = getViewRect(buttonM);
		rectComma = getViewRect(buttonComma);
		rectPeriod = getViewRect(buttonPeriod);
		rectSlash = getViewRect(buttonSlash);
		rectLeftClick = getViewRect(buttonLeftClick);
		rectRightClick = getViewRect(buttonRightClick);
		rectCtrl = getViewRect(toggleButtonCtrl);
		rectWin = getViewRect(toggleButtonWin);
		rectAlt = getViewRect(toggleButtonAlt);
		rectEsc = getViewRect(buttonEsc);
		rectSpace = getViewRect(buttonSpace);
		rectLeft = getViewRect(buttonLeft);
		rectUp = getViewRect(buttonUp);
		rectDown = getViewRect(buttonDown);
		rectRight = getViewRect(buttonRight);

		bInited2 = true;
	}

	public static void sendString(String string, String hostname) {
		try {
			DatagramSocket socket = new DatagramSocket();
			byte[] data = string.getBytes();
			DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(hostname), port);
			socket.send(packet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void processMouseButtons(int button, String hostname)
	{
		switch (button) {

			case MOUSE_LEFT_DOWN:
				if (!bLeftMouseButtonDown) {
					bLeftMouseButtonDown = true;
					sendString("MOUSE_LEFT_DOWN", hostname);
				}
				break;

			case MOUSE_LEFT_UP:
				if (bLeftMouseButtonDown) {
					bLeftMouseButtonDown = false;
					sendString("MOUSE_LEFT_UP", hostname);
				}
				break;

			case MOUSE_MIDDLE_DOWN:
				if (!bMiddleMouseButtonDown) {
					bMiddleMouseButtonDown = true;
					sendString("MOUSE_MIDDLE_DOWN", hostname);
				}
				break;

			case MOUSE_MIDDLE_UP:
				if (bMiddleMouseButtonDown) {
					bMiddleMouseButtonDown = false;
					sendString("MOUSE_MIDDLE_UP", hostname);
				}
				break;

			case MOUSE_RIGHT_DOWN:
				if (!bRightMouseButtonDown) {
					bRightMouseButtonDown = true;
					sendString("MOUSE_RIGHT_DOWN", hostname);
				}
				break;

			case MOUSE_RIGHT_UP:
				if (bRightMouseButtonDown) {
					bRightMouseButtonDown = false;
					sendString("MOUSE_RIGHT_UP", hostname);
				}
				break;

			default:
				break;
		}
	}

	public static void turnOffAllModifierKeys() {
		toggleButtonShift.setChecked(false);
		toggleButtonCtrl.setChecked(false);
		toggleButtonWin.setChecked(false);
		toggleButtonAlt.setChecked(false);
	}

	public static void processKeys(int keyCode, int character, int keyAction, String hostname) {
		if (keyCode == 0 && character == 0) {
			if (keyAction != KEY_UP)
				return;

			// We are told to release all keys.
			for (int i = 0; i < keyStreamsDown.size(); i++) {
				sendString("KEY_UP " + keyStreamsDown.get(i), hostname);
				keyStreamsDown.remove(keyStreamsDown.get(i));
			}
		}

		String szModifierKeys = new String();
		String szKeyStream = new String();

		// isModOn() is true by default, which means modifier keys
		// (e.g. SHIFT, CTRL, etc.) act as toggle buttons.
		//
		// If isModOn() is false, then modifier keys act as normal buttons
		// that can be pressed individually, even for a period of time.
		// This is useful in certain applications such as games.

		if (isModOn()) {
			if (toggleButtonShift.isChecked())
				szModifierKeys += "SHIFT ";
			else if (toggleButtonCtrl.isChecked())
				szModifierKeys += "CTRL ";
			else if (toggleButtonWin.isChecked())
				szModifierKeys += "WIN ";
			else if (toggleButtonAlt.isChecked())
				szModifierKeys += "ALT ";
		}

		switch (keyCode) {

			case KEYCODE_ESCAPE:
				szKeyStream = szModifierKeys + "ESC";
				break;

			case KeyEvent.KEYCODE_DEL:
				szKeyStream = szModifierKeys + "BACKSPACE";
				break;

			case KeyEvent.KEYCODE_TAB:
				szKeyStream = szModifierKeys + "TAB";
				break;

			case KEYCODE_CAPS_LOCK:
				szKeyStream = szModifierKeys + "CAPS";
				break;

			case KeyEvent.KEYCODE_ENTER:
				szKeyStream = szModifierKeys + "ENTER";
				break;

			case KeyEvent.KEYCODE_SPACE:
				szKeyStream = szModifierKeys + "SPACE";
				break;

			case KeyEvent.KEYCODE_DPAD_UP:
				szKeyStream = szModifierKeys + "UP";
				break;

			case KeyEvent.KEYCODE_DPAD_DOWN:
				szKeyStream = szModifierKeys + "DOWN";
				break;

			case KeyEvent.KEYCODE_DPAD_LEFT:
				szKeyStream = szModifierKeys + "LEFT";
				break;

			case KeyEvent.KEYCODE_DPAD_RIGHT:
				szKeyStream = szModifierKeys + "RIGHT";
				break;

			default:
				if (!isModOn() && (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT))
					szKeyStream = "SHIFT";
				else if (!isModOn() && (keyCode == KEYCODE_CTRL_LEFT))
					szKeyStream = "CTRL";
				else if (!isModOn() && (keyCode == KEYCODE_META_LEFT))
					szKeyStream = "WIN";
				else if (!isModOn() && (keyCode == KeyEvent.KEYCODE_ALT_LEFT))
					szKeyStream = "ALT";
				else {
					if (character < 32 || character > 126)
						return;
					szKeyStream = szModifierKeys + Character.toString((char)character);
				}

				break;
		}

		if (szKeyStream.isEmpty())
			return;

		if (keyAction == KEY_DOWN) {
			// We are given specific key(s) to press.
			if (keyStreamsDown.contains(szKeyStream))
				return;
			else {
				sendString("KEY_DOWN " + szKeyStream, hostname);
				keyStreamsDown.add(szKeyStream);
			}
		}
		else if (keyAction == KEY_UP) {
			// We are given specific key(s) to release.
			if (!keyStreamsDown.contains(szKeyStream))
				return;
			else {
				sendString("KEY_UP " + szKeyStream, hostname);
				keyStreamsDown.remove(szKeyStream);
			}
		}
	}
}
