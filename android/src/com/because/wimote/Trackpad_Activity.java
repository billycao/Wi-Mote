package com.because.wimote;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

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
	
	//MouseView view;
	
	// var for server I/O
	private int serverUdpPort;
	private String hostname;

	// if we want to have customizable gestures
	// GestureDetector gestureDetector = null;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set view
        System.out.println("beginning init\n");
        setContentView(R.layout.trackpad);
		screenWidth = 480;
		screenHeight = 800;
		
		xview = (TextView)findViewById(R.id.trackpad_textview1);
		yview = (TextView)findViewById(R.id.trackpad_textview2);
		statusText = (TextView)findViewById(R.id.trackpad_textview3);
		xview.setText(Integer.toString((int) currX));
    	yview.setText(Integer.toString((int) currY));
    	statusText.setText("Successful initialization");
    	
		ImageView temp = (ImageView)findViewById(R.id.trackpad_imageview1);
		temp.setOnTouchListener(this);
    }
    public boolean sendData(int x, int y)
    {
    	try {
			DatagramSocket socket = new DatagramSocket();
			ByteBuffer buffer = ByteBuffer.allocate(8);
			buffer.putInt(0, x);
			buffer.putInt(5, y);
			byte[] data = buffer.array();
			DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(hostname), serverUdpPort);
			socket.send(packet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    	return true;
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
    	float resx = deltax/(float)screenWidth;
    	float resy = deltax/(float)screenHeight;
    	// and now cast them to a %change in the original res
    	int perx = (int)resx*100;
    	int pery = (int)resy*100;
    	// bounds checking
    	if(perx > MAX_X)
    		perx = MAX_X;
    	if(pery > MAX_Y)
    		pery = MAX_Y;
    	// for now simply update the values in display boxes
    	xview.setText(Integer.toString((int)deltax));
    	yview.setText(Integer.toString((int)deltay));
    	// sendData(perx, pery);
    	return true;
    }
    /*
	public class MouseView extends SurfaceView implements Runnable{
		
		Thread t = null;
		SurfaceHolder holder;
		boolean ready = false;
		
		public MouseView(Context context){
			super(context);
			holder = getHolder();
		}
		
		public void run() {
			while(ready == true){
				// do something
				if(!holder.getSurface().isValid())
					continue;
				Canvas c = holder.lockCanvas();
				c.drawARGB(255,69,69,69);
				holder.unlockCanvasAndPost(c);
			}
		}
		
		public void pause(){
			ready = false;
			while(true){
				try{
					t.join();
				}
				catch(InterruptedException e){
					e.printStackTrace();
				}
				break;
			}
			t = null;
		}
		
		public void resume(){
			ready = true;
			t = new Thread(this);
			t.start();
		}
		public boolean onTouch(View arg0, MotionEvent arg1) {
			return super.onTouch(arg0, arg1);
	}
	*/
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
