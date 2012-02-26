package com.because.wimote;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

public class Trackpad_Activity extends Activity implements OnTouchListener{
    /** Called when the activity is first created. */
	// variables for mouse I/O
	private int MAX_X, MAX_Y = 30;
	private float currX, currY = -1f;
	private int screenHeight, screenWidth;
	private TextView xview, yview, statusText;
	private boolean multiTouch;
	
	//MouseView view;
	
	// var for server I/O
	private int serverUdpPort;
	private String hostname;
	String mouseData = new String();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set view
        System.out.println("beginning init\n");
        setContentView(R.layout.trackpad);
		screenWidth = 480;
		screenHeight = 800;
		multiTouch = false;
		xview = (TextView)findViewById(R.id.trackpad_textview1);
		yview = (TextView)findViewById(R.id.trackpad_textview2);
		statusText = (TextView)findViewById(R.id.trackpad_textview3);
		xview.setText(Integer.toString((int) currX));
    	yview.setText(Integer.toString((int) currY));
    	statusText.setText("Successful initialization");
    	
		ImageView temp = (ImageView)findViewById(R.id.trackpad_imageview1);
		temp.setOnTouchListener(this);
    }
	
	private void sendString(String string) {
			try {
				DatagramSocket socket = new DatagramSocket();
				byte[] data = string.getBytes();
				DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(hostname), serverUdpPort);
				socket.send(packet);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
    	float perx = deltax/(float)screenWidth * 100;
    	float pery = deltax/(float)screenHeight * 100;

    	// bounds checking
    	if(perx > MAX_X)
    		perx = MAX_X;
    	else if(perx < -MAX_X)
    		perx = -MAX_X;
    	if(pery > MAX_Y)
    		pery = MAX_Y;
    	else if(pery < -MAX_Y)
    		pery = -MAX_Y;
    	
    	// for now simply update the values in display boxes
    	xview.setText(Integer.toString((int)deltax));
    	yview.setText(Integer.toString((int)deltay));
        if(multiTouch == true)
    	{
    		// TODO create new buffer to indicate the changes are scrolling related
    		statusText.setText("Scrolling swag");
    		float scroll_dir = Math.max(Math.abs(pery), Math.abs(pery));
    		mouseData = Float.toString(scroll_dir);
	    	sendString("MOUSE_SCROLL " + mouseData);
    	}
        else
        {
	        mouseData = Float.toString(perx) + " " + Float.toString(pery);
	    	sendString("ACCEL " + mouseData);
        }
    	return true;
    }
    
	public boolean onTouch(View arg0, MotionEvent arg1) {
		try {
			Thread.sleep(50);
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
