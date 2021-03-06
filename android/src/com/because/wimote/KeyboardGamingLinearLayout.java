package com.because.wimote;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * TODO jdoc it? 
 *
 */
public class KeyboardGamingLinearLayout extends LinearLayout {

	private WiMoteUtil util;

	public KeyboardGamingLinearLayout(Context context) {
		super(context);
		util = new WiMoteUtil((Activity)context, true);
	}

	public KeyboardGamingLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		util = new WiMoteUtil((Activity)context, true);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
				util.processButtons(event, true);
				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				util.processButtons(event, false);
				break;

			default:
				break;
		}

		return super.onInterceptTouchEvent(event);
	}
}
