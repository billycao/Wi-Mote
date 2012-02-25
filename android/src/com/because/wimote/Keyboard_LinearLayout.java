package com.because.wimote;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class Keyboard_LinearLayout extends LinearLayout {

	private static ArrayList<Point> pointers = new ArrayList<Point>();

	public Keyboard_LinearLayout(Context context) {
		super(context);
	}

	public Keyboard_LinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private boolean isButtonPressed(Rect rectButton) {
		for (int i = 0; i < pointers.size(); i++) {
			if (rectButton.contains(pointers.get(i).x, pointers.get(i).y))
				return true;
		}
		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
		String hostname = settings.getString("hostname", "192.168.1.101");
		
		if (!Keyboard_Util.isInited())
			return super.onInterceptTouchEvent(event);

		if (!Keyboard_Util.isInited2())
			Keyboard_Util.initVirtualKeyboard2();
		
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				// Support for NKRO (limited by capacitive touch sensor).

				pointers.clear();

				for (int i = 0; i < event.getPointerCount(); i++) {
					Point point = new Point();
					point.x = (int)event.getX(i);
					point.y = (int)event.getY(i);
					pointers.add(point);
				}

				if (isButtonPressed(Keyboard_Util.rectGrave)) {
					Keyboard_Util.buttonGrave.setPressed(true);
					Keyboard_Util.processKeys(0, '`', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonGrave.setPressed(false);
					Keyboard_Util.processKeys(0, '`', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectOne)) {
					Keyboard_Util.buttonOne.setPressed(true);
					Keyboard_Util.processKeys(0, '1', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonOne.setPressed(false);
					Keyboard_Util.processKeys(0, '1', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectTwo)) {
					Keyboard_Util.buttonTwo.setPressed(true);
					Keyboard_Util.processKeys(0, '2', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonTwo.setPressed(false);
					Keyboard_Util.processKeys(0, '2', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectThree)) {
					Keyboard_Util.buttonThree.setPressed(true);
					Keyboard_Util.processKeys(0, '3', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonThree.setPressed(false);
					Keyboard_Util.processKeys(0, '3', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectFour)) {
					Keyboard_Util.buttonFour.setPressed(true);
					Keyboard_Util.processKeys(0, '4', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonFour.setPressed(false);
					Keyboard_Util.processKeys(0, '4', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectFive)) {
					Keyboard_Util.buttonFive.setPressed(true);
					Keyboard_Util.processKeys(0, '5', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonFive.setPressed(false);
					Keyboard_Util.processKeys(0, '5', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectSix)) {
					Keyboard_Util.buttonSix.setPressed(true);
					Keyboard_Util.processKeys(0, '6', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonSix.setPressed(false);
					Keyboard_Util.processKeys(0, '6', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectSeven)) {
					Keyboard_Util.buttonSeven.setPressed(true);
					Keyboard_Util.processKeys(0, '7', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonSeven.setPressed(false);
					Keyboard_Util.processKeys(0, '7', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectEight)) {
					Keyboard_Util.buttonEight.setPressed(true);
					Keyboard_Util.processKeys(0, '8', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonEight.setPressed(false);
					Keyboard_Util.processKeys(0, '8', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectNine)) {
					Keyboard_Util.buttonNine.setPressed(true);
					Keyboard_Util.processKeys(0, '9', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonNine.setPressed(false);
					Keyboard_Util.processKeys(0, '9', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectZero)) {
					Keyboard_Util.buttonZero.setPressed(true);
					Keyboard_Util.processKeys(0, '0', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonZero.setPressed(false);
					Keyboard_Util.processKeys(0, '0', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectDash)) {
					Keyboard_Util.buttonDash.setPressed(true);
					Keyboard_Util.processKeys(0, '-', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonDash.setPressed(false);
					Keyboard_Util.processKeys(0, '-', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectEquals)) {
					Keyboard_Util.buttonEquals.setPressed(true);
					Keyboard_Util.processKeys(0, '=', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonEquals.setPressed(false);
					Keyboard_Util.processKeys(0, '=', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectBackspace)) {
					Keyboard_Util.buttonBackspace.setPressed(true);
					Keyboard_Util.processKeys(KeyEvent.KEYCODE_DEL, 0, Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonBackspace.setPressed(false);
					Keyboard_Util.processKeys(KeyEvent.KEYCODE_DEL, 0, Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectTab)) {
					Keyboard_Util.buttonTab.setPressed(true);
					Keyboard_Util.processKeys(KeyEvent.KEYCODE_TAB, 0, Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonTab.setPressed(false);
					Keyboard_Util.processKeys(KeyEvent.KEYCODE_TAB, 0, Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectQ)) {
					Keyboard_Util.buttonQ.setPressed(true);
					Keyboard_Util.processKeys(0, 'q', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonQ.setPressed(false);
					Keyboard_Util.processKeys(0, 'q', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectW)) {
					Keyboard_Util.buttonW.setPressed(true);
					Keyboard_Util.processKeys(0, 'w', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonW.setPressed(false);
					Keyboard_Util.processKeys(0, 'w', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectE)) {
					Keyboard_Util.buttonE.setPressed(true);
					Keyboard_Util.processKeys(0, 'e', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonE.setPressed(false);
					Keyboard_Util.processKeys(0, 'e', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectR)) {
					Keyboard_Util.buttonR.setPressed(true);
					Keyboard_Util.processKeys(0, 'r', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonR.setPressed(false);
					Keyboard_Util.processKeys(0, 'r', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectT)) {
					Keyboard_Util.buttonT.setPressed(true);
					Keyboard_Util.processKeys(0, 't', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonT.setPressed(false);
					Keyboard_Util.processKeys(0, 't', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectY)) {
					Keyboard_Util.buttonY.setPressed(true);
					Keyboard_Util.processKeys(0, 'y', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonY.setPressed(false);
					Keyboard_Util.processKeys(0, 'y', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectU)) {
					Keyboard_Util.buttonU.setPressed(true);
					Keyboard_Util.processKeys(0, 'u', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonU.setPressed(false);
					Keyboard_Util.processKeys(0, 'u', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectI)) {
					Keyboard_Util.buttonI.setPressed(true);
					Keyboard_Util.processKeys(0, 'i', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonI.setPressed(false);
					Keyboard_Util.processKeys(0, 'i', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectO)) {
					Keyboard_Util.buttonO.setPressed(true);
					Keyboard_Util.processKeys(0, 'o', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonO.setPressed(false);
					Keyboard_Util.processKeys(0, 'o', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectP)) {
					Keyboard_Util.buttonP.setPressed(true);
					Keyboard_Util.processKeys(0, 'p', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonP.setPressed(false);
					Keyboard_Util.processKeys(0, 'p', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectLeftSquareBracket)) {
					Keyboard_Util.buttonLeftSquareBracket.setPressed(true);
					Keyboard_Util.processKeys(0, '[', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonLeftSquareBracket.setPressed(false);
					Keyboard_Util.processKeys(0, '[', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectRightSquareBracket)) {
					Keyboard_Util.buttonRightSquareBracket.setPressed(true);
					Keyboard_Util.processKeys(0, ']', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonRightSquareBracket.setPressed(false);
					Keyboard_Util.processKeys(0, ']', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectBackslash)) {
					Keyboard_Util.buttonBackslash.setPressed(true);
					Keyboard_Util.processKeys(0, '\\', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonBackslash.setPressed(false);
					Keyboard_Util.processKeys(0, '\\', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectCapsLock)) {
					Keyboard_Util.buttonCapsLock.setPressed(true);
					Keyboard_Util.processKeys(Keyboard_Util.KEYCODE_CAPS_LOCK, 0, Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonCapsLock.setPressed(false);
					Keyboard_Util.processKeys(Keyboard_Util.KEYCODE_CAPS_LOCK, 0, Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectA)) {
					Keyboard_Util.buttonA.setPressed(true);
					Keyboard_Util.processKeys(0, 'a', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonA.setPressed(false);
					Keyboard_Util.processKeys(0, 'a', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectS)) {
					Keyboard_Util.buttonS.setPressed(true);
					Keyboard_Util.processKeys(0, 's', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonS.setPressed(false);
					Keyboard_Util.processKeys(0, 's', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectD)) {
					Keyboard_Util.buttonD.setPressed(true);
					Keyboard_Util.processKeys(0, 'd', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonD.setPressed(false);
					Keyboard_Util.processKeys(0, 'd', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectF)) {
					Keyboard_Util.buttonF.setPressed(true);
					Keyboard_Util.processKeys(0, 'f', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonF.setPressed(false);
					Keyboard_Util.processKeys(0, 'f', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectG)) {
					Keyboard_Util.buttonG.setPressed(true);
					Keyboard_Util.processKeys(0, 'g', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonG.setPressed(false);
					Keyboard_Util.processKeys(0, 'g', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectH)) {
					Keyboard_Util.buttonH.setPressed(true);
					Keyboard_Util.processKeys(0, 'h', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonH.setPressed(false);
					Keyboard_Util.processKeys(0, 'h', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectJ)) {
					Keyboard_Util.buttonJ.setPressed(true);
					Keyboard_Util.processKeys(0, 'j', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonJ.setPressed(false);
					Keyboard_Util.processKeys(0, 'j', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectK)) {
					Keyboard_Util.buttonK.setPressed(true);
					Keyboard_Util.processKeys(0, 'k', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonK.setPressed(false);
					Keyboard_Util.processKeys(0, 'k', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectL)) {
					Keyboard_Util.buttonL.setPressed(true);
					Keyboard_Util.processKeys(0, 'l', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonL.setPressed(false);
					Keyboard_Util.processKeys(0, 'l', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectSemicolon)) {
					Keyboard_Util.buttonSemicolon.setPressed(true);
					Keyboard_Util.processKeys(0, ';', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonSemicolon.setPressed(false);
					Keyboard_Util.processKeys(0, ';', Keyboard_Util.KEY_UP, hostname);
				}
				
				if (isButtonPressed(Keyboard_Util.rectApostrophe)) {
					Keyboard_Util.buttonApostrophe.setPressed(true);
					Keyboard_Util.processKeys(0, '\'', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonApostrophe.setPressed(false);
					Keyboard_Util.processKeys(0, '\'', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectEnter)) {
					Keyboard_Util.buttonEnter.setPressed(true);
					Keyboard_Util.processKeys(KeyEvent.KEYCODE_ENTER, 0, Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonEnter.setPressed(false);
					Keyboard_Util.processKeys(KeyEvent.KEYCODE_ENTER, 0, Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectZ)) {
					Keyboard_Util.buttonZ.setPressed(true);
					Keyboard_Util.processKeys(0, 'z', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonZ.setPressed(false);
					Keyboard_Util.processKeys(0, 'z', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectX)) {
					Keyboard_Util.buttonX.setPressed(true);
					Keyboard_Util.processKeys(0, 'x', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonX.setPressed(false);
					Keyboard_Util.processKeys(0, 'x', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectC)) {
					Keyboard_Util.buttonC.setPressed(true);
					Keyboard_Util.processKeys(0, 'c', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonC.setPressed(false);
					Keyboard_Util.processKeys(0, 'c', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectV)) {
					Keyboard_Util.buttonV.setPressed(true);
					Keyboard_Util.processKeys(0, 'v', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonV.setPressed(false);
					Keyboard_Util.processKeys(0, 'v', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectB)) {
					Keyboard_Util.buttonB.setPressed(true);
					Keyboard_Util.processKeys(0, 'b', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonB.setPressed(false);
					Keyboard_Util.processKeys(0, 'b', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectN)) {
					Keyboard_Util.buttonN.setPressed(true);
					Keyboard_Util.processKeys(0, 'n', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonN.setPressed(false);
					Keyboard_Util.processKeys(0, 'n', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectM)) {
					Keyboard_Util.buttonM.setPressed(true);
					Keyboard_Util.processKeys(0, 'm', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonM.setPressed(false);
					Keyboard_Util.processKeys(0, 'm', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectComma)) {
					Keyboard_Util.buttonComma.setPressed(true);
					Keyboard_Util.processKeys(0, ',', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonComma.setPressed(false);
					Keyboard_Util.processKeys(0, ',', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectPeriod)) {
					Keyboard_Util.buttonPeriod.setPressed(true);
					Keyboard_Util.processKeys(0, '.', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonPeriod.setPressed(false);
					Keyboard_Util.processKeys(0, '.', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectSlash)) {
					Keyboard_Util.buttonSlash.setPressed(true);
					Keyboard_Util.processKeys(0, '/', Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonSlash.setPressed(false);
					Keyboard_Util.processKeys(0, '/', Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectLeftClick)) {
					Keyboard_Util.buttonLeftClick.setPressed(true);
					Keyboard_Util.processMouseButtons(Keyboard_Util.MOUSE_LEFT_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonLeftClick.setPressed(false);
					Keyboard_Util.processMouseButtons(Keyboard_Util.MOUSE_LEFT_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectRightClick)) {
					Keyboard_Util.buttonRightClick.setPressed(true);
					Keyboard_Util.processMouseButtons(Keyboard_Util.MOUSE_RIGHT_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonRightClick.setPressed(false);
					Keyboard_Util.processMouseButtons(Keyboard_Util.MOUSE_RIGHT_UP, hostname);
				}

				if (!Keyboard_Util.isModOn()) {
					// Allow multitouch of modifier keys only when they are not
					// acting as toggle switches.

					if (isButtonPressed(Keyboard_Util.rectShift)) {
						Keyboard_Util.toggleButtonShift.setPressed(true);
						Keyboard_Util.processKeys(KeyEvent.KEYCODE_SHIFT_LEFT, 0, Keyboard_Util.KEY_DOWN, hostname);
					}
					else {
						Keyboard_Util.toggleButtonShift.setPressed(false);
						Keyboard_Util.processKeys(KeyEvent.KEYCODE_SHIFT_LEFT, 0, Keyboard_Util.KEY_UP, hostname);
					}

					if (isButtonPressed(Keyboard_Util.rectCtrl)) {
						Keyboard_Util.toggleButtonCtrl.setPressed(true);
						Keyboard_Util.processKeys(Keyboard_Util.KEYCODE_CTRL_LEFT, 0, Keyboard_Util.KEY_DOWN, hostname);
					}
					else {
						Keyboard_Util.toggleButtonCtrl.setPressed(false);
						Keyboard_Util.processKeys(Keyboard_Util.KEYCODE_CTRL_LEFT, 0, Keyboard_Util.KEY_UP, hostname);
					}
	
					if (isButtonPressed(Keyboard_Util.rectWin)) {
						Keyboard_Util.toggleButtonWin.setPressed(true);
						Keyboard_Util.processKeys(Keyboard_Util.KEYCODE_META_LEFT, 0, Keyboard_Util.KEY_DOWN, hostname);
					}
					else {
						Keyboard_Util.toggleButtonWin.setPressed(false);
						Keyboard_Util.processKeys(Keyboard_Util.KEYCODE_META_LEFT, 0, Keyboard_Util.KEY_UP, hostname);
					}
	
					if (isButtonPressed(Keyboard_Util.rectAlt)) {
						Keyboard_Util.toggleButtonAlt.setPressed(true);
						Keyboard_Util.processKeys(KeyEvent.KEYCODE_ALT_LEFT, 0, Keyboard_Util.KEY_DOWN, hostname);
					}
					else {
						Keyboard_Util.toggleButtonAlt.setPressed(false);
						Keyboard_Util.processKeys(KeyEvent.KEYCODE_ALT_LEFT, 0, Keyboard_Util.KEY_UP, hostname);
					}
				}

				if (isButtonPressed(Keyboard_Util.rectEsc)) {
					Keyboard_Util.buttonEsc.setPressed(true);
					Keyboard_Util.processKeys(Keyboard_Util.KEYCODE_ESCAPE, 0, Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonEsc.setPressed(false);
					Keyboard_Util.processKeys(Keyboard_Util.KEYCODE_ESCAPE, 0, Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectSpace)) {
					Keyboard_Util.buttonSpace.setPressed(true);
					Keyboard_Util.processKeys(KeyEvent.KEYCODE_SPACE, 0, Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonSpace.setPressed(false);
					Keyboard_Util.processKeys(KeyEvent.KEYCODE_SPACE, 0, Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectLeft)) {
					Keyboard_Util.buttonLeft.setPressed(true);
					Keyboard_Util.processKeys(KeyEvent.KEYCODE_DPAD_LEFT, 0, Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonLeft.setPressed(false);
					Keyboard_Util.processKeys(KeyEvent.KEYCODE_DPAD_LEFT, 0, Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectUp)) {
					Keyboard_Util.buttonUp.setPressed(true);
					Keyboard_Util.processKeys(KeyEvent.KEYCODE_DPAD_UP, 0, Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonUp.setPressed(false);
					Keyboard_Util.processKeys(KeyEvent.KEYCODE_DPAD_UP, 0, Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectDown)) {
					Keyboard_Util.buttonDown.setPressed(true);
					Keyboard_Util.processKeys(KeyEvent.KEYCODE_DPAD_DOWN, 0, Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonDown.setPressed(false);
					Keyboard_Util.processKeys(KeyEvent.KEYCODE_DPAD_DOWN, 0, Keyboard_Util.KEY_UP, hostname);
				}

				if (isButtonPressed(Keyboard_Util.rectRight)) {
					Keyboard_Util.buttonRight.setPressed(true);
					Keyboard_Util.processKeys(KeyEvent.KEYCODE_DPAD_RIGHT, 0, Keyboard_Util.KEY_DOWN, hostname);
				}
				else {
					Keyboard_Util.buttonRight.setPressed(false);
					Keyboard_Util.processKeys(KeyEvent.KEYCODE_DPAD_RIGHT, 0, Keyboard_Util.KEY_UP, hostname);
				}

				break;

			case MotionEvent.ACTION_UP:
				Keyboard_Util.processKeys(0, 0, Keyboard_Util.KEY_UP, hostname);
				Keyboard_Util.processMouseButtons(Keyboard_Util.MOUSE_LEFT_UP, hostname);
				Keyboard_Util.processMouseButtons(Keyboard_Util.MOUSE_MIDDLE_UP, hostname);
				Keyboard_Util.processMouseButtons(Keyboard_Util.MOUSE_RIGHT_UP, hostname);
				Keyboard_Util.resetVirtualKeyboard();
				break;

			default:
				break;
		}

		return super.onInterceptTouchEvent(event);
	}
}
