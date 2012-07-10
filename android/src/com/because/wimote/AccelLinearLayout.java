package com.because.wimote;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
//import android.widget.ToggleButton;

/**
 * TODO jdoc it? 
 *
 */
public class AccelLinearLayout extends LinearLayout {

	private Activity activity;
	private WiMoteUtil util;

	private HashMap<Integer, Point> pointers = new HashMap<Integer, Point>();

	public AccelLinearLayout(Context context) {
		super(context);
		activity = (Activity)context;
		util = new WiMoteUtil(activity);
	}

	public AccelLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		activity = (Activity)context;
		util = new WiMoteUtil(activity);
	}

	private Rect getViewRect(View view) {
		Rect rectParent = new Rect();
		//((Accel_LinearLayout)activity.findViewById(R.id.Parent)).getGlobalVisibleRect(rectParent);

		Rect rect = new Rect();
		view.getGlobalVisibleRect(rect);
		rect.offset(-rectParent.left, -rectParent.top);

		return rect;
	}

	private void processButtons(MotionEvent event, boolean bPressed) {
		int pointerIndex = event.getActionIndex();
		int pointerId = event.getPointerId(pointerIndex);
		Point point = bPressed ? (new Point((int)event.getX(pointerIndex), (int)event.getY(pointerIndex))) : pointers.get(pointerId);
		if (point == null)
			return;

		String keyAction = bPressed ? WiMoteUtil.KEY_DOWN : WiMoteUtil.KEY_UP;

		
		for (int i = 0; i < WiMoteUtil.ACCEL_KEY_IDS.length; i++) {
			if (getViewRect(activity.findViewById(WiMoteUtil.ACCEL_KEY_IDS[i])).contains(point.x, point.y)) {
				activity.findViewById(WiMoteUtil.ACCEL_KEY_IDS[i]).setPressed(bPressed);
				util.processKeys(keyAction, util.getKeyName(WiMoteUtil.ACCEL_KEY_IDS[i]), false);
				if (bPressed)
					pointers.put(event.getPointerId(pointerIndex), point);
				else
					pointers.remove(pointerId);
			}
		}
		
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
				processButtons(event, true);
				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				processButtons(event, false);
				break;

			default:
				break;
		}

		return super.onInterceptTouchEvent(event);
	}
}
