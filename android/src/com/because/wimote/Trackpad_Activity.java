package com.because.wimote;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Trackpad_Activity extends Activity implements OnTouchListener{
    /** Called when the activity is first created. */
	// variables for mouse I/O
	private float currX, currY, screenHeight, screenWidth, MAX;
	private boolean multiTouch, leftClick, rightClick;
	private WiMoteUtil util;
	SharedPreferences.Editor editPref;
	
	// variables for server I/O
	String mouseData = new String();
	
	// variables for testing
	private TextView xview, yview, statusText;
	
	private OnTouchListener buttonListener = new OnTouchListener() {

		public boolean onTouch(View button, MotionEvent event) {
			boolean ret = false;
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (button.getId() == R.id.trackpad_left && rightClick == false){
					statusText.setText("Left Click Detected");
					util.sendString("MOUSE_LEFT_DOWN");
					ret = leftClick = true;
				}
				else if (button.getId() == R.id.trackpad_right && leftClick == false){
					statusText.setText("Right Click Detected");
					util.sendString("MOUSE_RIGHT_DOWN");
					ret = rightClick = true;
				}
				break;
			case MotionEvent.ACTION_UP:
				if (button.getId() == R.id.trackpad_left && leftClick == true){
					statusText.setText("Left Release Detected");
					util.sendString("MOUSE_LEFT_UP");
					leftClick = false;
					ret = true;
				}
				else if (button.getId() == R.id.trackpad_right && rightClick == true){
					statusText.setText("Right Release Detected");
					util.sendString("MOUSE_RIGHT_UP");
					rightClick = false;
					ret = true;
				}
				break;
			default:
				break;
			}
			
			return ret;
		}
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set view
        System.out.println("beginning init\n");
        setContentView(R.layout.trackpad);
        
        // screen interface variables
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
		screenWidth = display.widthPixels;
		screenHeight = (float)display.heightPixels * 0.8f;
		MAX = 30f;
		
		// touch variables
		((Button)findViewById(R.id.trackpad_left)).setOnTouchListener(buttonListener);
        ((Button)findViewById(R.id.trackpad_right)).setOnTouchListener(buttonListener);
        ((ImageView)findViewById(R.id.trackpad_imageview1)).setOnTouchListener(this);
        
        
		// helper variables
		multiTouch = leftClick = rightClick = false;
		util = new WiMoteUtil(this);
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		editPref = settings.edit();
		
		// set text
		xview = (TextView)findViewById(R.id.trackpad_textview1);
		yview = (TextView)findViewById(R.id.trackpad_textview2);
		statusText = (TextView)findViewById(R.id.trackpad_textview3);
		xview.setText(Integer.toString((int) currX));
    	yview.setText(Integer.toString((int) currY));
    	statusText.setText("Successful initialization");
    }
	
    private boolean ProcessData(float x, float y)
    {
    	// 
    	float deltax = currX - x;
    	float deltay = currY - y;
    	currX = x;
    	currY = y;
    	if(deltax == 0 && deltay == 0)    	    	
    		return false;
    	float perx = deltax; ///screenWidth * 100;
    	float pery = deltay; ///screenHeight * 100;

    	// bounds checking
    	/*
    	if(perx > MAX)
    		perx = MAX;
    	else if(perx < -MAX)
    		perx = -MAX;
    	if(pery > MAX)
    		pery = MAX;
    	else if(pery < -MAX)
    		pery = -MAX;
    	*/
    	
    	//replace 1.5 with sensitituty
    	perx = (float) (Math.min(perx, MAX)*1.5); //*track_Sensitivity;
    	pery = (float) (Math.min(pery, MAX)*1.5); //*track_Sensitivity;
    	
    	perx = (float) (Math.max(perx, -MAX)*1.5); //*track_Sensitivity;
    	pery = (float) (Math.max(pery, -MAX)*1.5); //*track_Sensitivity;

    	//orientation fix? confirmed, -1 fixed direction Stays fixed even if screen rotates..
    	perx = perx*-1; 
    	pery = pery*-1;
    	
    	
    	// for now simply update the values in display boxes
    	xview.setText("deltaX: " + Integer.toString((int)perx));
    	yview.setText("deltaY: " + Integer.toString((int)pery));
        
    	// beginning of gestures
    	if(multiTouch == true)
    	{
    		statusText.setText("Scrolling swag");
    		
    		if(Math.abs(perx) > Math.abs(pery))
    		{
    			mouseData = Float.toString(perx);
    	    	util.sendString("MOUSE_HSCROLL " + mouseData);
    		}
    		else
    		{
    			mouseData = Float.toString(pery);
    	    	util.sendString("MOUSE_SCROLL " + mouseData);
    		}
    	}
    	// single finger motion
        else
        {
	        mouseData = Float.toString(perx) + " " + Float.toString(pery);
	    	util.sendString("MOUSE_DELTA " + mouseData);
        }
    	return true;
    }
    
	public boolean onTouch(View arg0, MotionEvent arg1) {
		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean success = false;
		float perx = arg1.getX()*arg1.getXPrecision();
		float pery = arg1.getY()*arg1.getYPrecision();
			
		xview.setText(Integer.toString((int) perx));
    	yview.setText(Integer.toString((int) pery));
		switch(arg1.getAction())
		{
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_DOWN:
			statusText.setText("Single touch");
			currX = perx;
			currY = pery;
			success = true;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			multiTouch = true;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			multiTouch = false;
			break;
		case MotionEvent.ACTION_MOVE:
			statusText.setText("Motion");
			success = ProcessData(perx, pery);
			break;
		default:
			statusText.setText("Other touch type detected!");
			success = true;
			break;
		}
		return success;
	}
}
