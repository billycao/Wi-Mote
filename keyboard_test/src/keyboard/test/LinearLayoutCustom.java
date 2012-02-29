package keyboard.test;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class LinearLayoutCustom extends LinearLayout {

	private static Activity activity;
	private WiMoteUtil util;

	private static HashMap<Integer, Point> pointers = new HashMap<Integer, Point>();

	public LinearLayoutCustom(Context context) {
		super(context);
		activity = (Activity)context;
		util = new WiMoteUtil(activity);
	}

	public LinearLayoutCustom(Context context, AttributeSet attrs) {
		super(context, attrs);
		activity = (Activity)context;
		util = new WiMoteUtil(activity);
	}

	private Rect getViewRect(View view) {
		Rect rectParent = new Rect();
		((LinearLayoutCustom)activity.findViewById(R.id.Parent)).getGlobalVisibleRect(rectParent);

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

		if (!((ToggleButton)activity.findViewById(R.id.ToggleModifiers)).isChecked()) {
			// Allow multitouch of modifier keys only when they are not acting
			// as toggle buttons.
			for (int i = 0; i < WiMoteUtil.MODIFIER_KEY_IDS.length; i++) {
				if (getViewRect(activity.findViewById(WiMoteUtil.MODIFIER_KEY_IDS[i])).contains(point.x, point.y)) {
					activity.findViewById(WiMoteUtil.MODIFIER_KEY_IDS[i]).setPressed(bPressed);
					util.processKeys(keyAction, WiMoteUtil.getModifierKeyName(WiMoteUtil.MODIFIER_KEY_IDS[i]));
					if (bPressed)
						pointers.put(event.getPointerId(pointerIndex), point);
					else
						pointers.remove(pointerId);
				}
			}
		}

		for (int i = 0; i < WiMoteUtil.KEY_IDS.length; i++) {
			if (getViewRect(activity.findViewById(WiMoteUtil.KEY_IDS[i])).contains(point.x, point.y)) {
				activity.findViewById(WiMoteUtil.KEY_IDS[i]).setPressed(bPressed);
				util.processKeys(keyAction, WiMoteUtil.getKeyName(WiMoteUtil.KEY_IDS[i]));
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
