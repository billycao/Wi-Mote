
package accel.test;
/*
 *Saqib's Accelerometer test
 */


import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.widget.TextView;
import android.widget.Button;
import android.widget.ToggleButton;

//extra for graphics
import android.graphics.*;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.view.View;
import android.view.*;


public class Accel_testActivity extends Activity implements OnClickListener  {

	private SensorManager mSensorManager;
	private List<Sensor> sensors;
	private Sensor mAccelerometer;
    
    private float mSensorX, mSensorY, mSensorZ = 0f;
    private float PercentX, PercentY, PercentZ = 0f;
    
    private TextView mTextView1, mTextView2;
    private TextView mSensativity, mThreshold;
    ToggleButton toggleRun;
    
    //For Accel________________________
	float PercentScaleMax = (float) 4; 
	float AccelMaximum = (float) 10;
	float DetectionThreshold = (float) 2;
    
    //For Graphics________________________
    DrawView drawView;    
    int width = 480;
    int height = 800;

    //for server________________________________________
    String szAccelPercent = new String();

	private static final String hostname = "192.168.0.6";
	private static final int port = 27015;
	
	private OnTouchListener buttonListener = new OnTouchListener() {

		public boolean onTouch(View button, MotionEvent event) {
			
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (button.getId() == R.id.Mouse_left)
					sendString("MOUSE_LEFT_DOWN");
				else if (button.getId() == R.id.Mouse_Right)
					sendString("MOUSE_RIGHT_DOWN");
				break;
			case MotionEvent.ACTION_UP:
				if (button.getId() == R.id.Mouse_left)
					sendString("MOUSE_LEFT_UP");
				else if (button.getId() == R.id.Mouse_Right)
					sendString("MOUSE_RIGHT_UP");
				break;
			default:
				break;
			}
			
			return false;
		}
	};
	
    /** Called when the activity is first created. */
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accel_test);
        
        //testing output
        mTextView1 = (TextView)findViewById(R.id.textView1);
        mTextView2 = (TextView)findViewById(R.id.textView2);
        
        mSensativity = (TextView) findViewById(R.id.sens_text);
        mThreshold = (TextView) findViewById(R.id.thres_text);
        
        Button buttonSensUp = (Button) findViewById(R.id.sens_button_up);
        buttonSensUp.setOnClickListener(this);
        Button buttonSensDn = (Button) findViewById(R.id.sens_button_down);
        buttonSensDn.setOnClickListener(this);
        Button buttonThresUp = (Button) findViewById(R.id.thres_button_up);
        buttonThresUp.setOnClickListener(this);
        Button buttonThresDn = (Button) findViewById(R.id.thres_button_down);
        buttonThresDn.setOnClickListener(this);
        
        ((Button)findViewById(R.id.Mouse_left)).setOnTouchListener(buttonListener);
        ((Button)findViewById(R.id.Mouse_Right)).setOnTouchListener(buttonListener);

        
        toggleRun = (ToggleButton)findViewById(R.id.toggleButton1);
        
        //for Graphics
        drawView = new DrawView(this);
        // drawView = (DrawView)findViewById(R.id.drawView1);
        drawView.setBackgroundColor(Color.BLACK);
        //  setContentView(drawView); // uncomment this for visual mode.
        
        //set the sensors and manager
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(sensors.size() >0)
        {
        	mAccelerometer = sensors.get(0);
        }
        
    }
    
    // SERVER  ------------------------------------------------------------
    private void sendString(String string) {
    	if(toggleRun.isChecked())
    	{
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
	}
    //-------------------------------------------------------------------  
    //+++++++++++++++++++BUTTON+++++++++++++++++++++++++++++++++++++++++++++++++
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sens_button_up:
			PercentScaleMax = (float) Math.max((PercentScaleMax +.2), 0);
			break;
		case R.id.sens_button_down:
			PercentScaleMax = (float) Math.max((PercentScaleMax -.2), 0);
			break;
		case R.id.thres_button_up:
			DetectionThreshold = (float) Math.max((DetectionThreshold +.2), 0);
			break;
		case R.id.thres_button_down:
			DetectionThreshold = (float) Math.max((DetectionThreshold -.2), 0);
			break;
		
		}
	}
	//+++++++++++++++++++BUTTON+++++++++++++++++++++++++++++++++++++++++++++++++
	
    private void ProcessData(float x, float y, float z)
    {
    	//float PercentScaleMax = (float) 4; 
    	//float AccelMaximum = (float) 10;
    	//float DetectionThreshold = (float) 2;
    
    	//under normal operation of the accelerometer the tilting of the phone to a 90* angle will 
    	//correspond with values in the range of 0->10, however if the phone is shaken or moved quickly
    	//the value may exceed 10, so the .min function will limit the value to 10, this is then scaled 
    	//up to 30, representing 30% of the screen traveled in a unit of time (TBD)
    	
    	//Scale and limit the Accelerometer data to a range between 0->30 (representing percent value for mouse movement)
    	PercentX = Math.min(x, AccelMaximum)*PercentScaleMax;
    	PercentY = Math.min(y, AccelMaximum)*PercentScaleMax;;
    	PercentZ = Math.min(z, AccelMaximum)*PercentScaleMax;
    	
    	//to ignore noise while holding the phone still.
    	if(Math.abs(PercentX) < DetectionThreshold)
    		PercentX = 0;
    	else // the threshold is subtracted (or added) to the Percent value to scale it back for the threshold
    		PercentX = PercentX - DetectionThreshold*(PercentX/Math.abs(PercentX));
    	
    	if(Math.abs(PercentY) < DetectionThreshold)
    		PercentY = 0;
    	else
    		PercentY = PercentY - DetectionThreshold*(PercentY/Math.abs(PercentY));
    	
    	if(Math.abs(PercentZ) < DetectionThreshold)
    		PercentZ = 0;
    	else
    		PercentZ = PercentZ - DetectionThreshold*(PercentZ/Math.abs(PercentZ));
    	
    	mTextView1.setText("%-X: " + PercentX + ";\n %-Y:" + PercentY +";\n %-Z:" + PercentZ);
    	
    	//---------------Pass Accelerometer data to Network Function------------------ 	
    	int RoundX =  -Math.round(PercentX);
    	int RoundY =  Math.round(PercentY);
 
    //	szAccelPercent = Integer.toString(RoundX, 10) + " " + Integer.toString(RoundY, 10);
    	szAccelPercent = Float.toString(-PercentX) + " " + Float.toString(PercentY);
    	
    	mTextView2.setText("ACCEL " + szAccelPercent);
    	sendString("ACCEL " + szAccelPercent);

    	//-------------Server Passing complete---------------------------------------------
    	
    	//===================== display data on view=============================
    	// adjust values to display on screen. 
    	float CurrX = x - mSensorX *10;
    	float CurrY = y - mSensorY *10;
    	float CurrZ = z - mSensorZ *10;
    
    	mSensativity.setText("" + Math.rint(10*PercentScaleMax)/10);
    	mThreshold.setText("" + Math.rint(10*DetectionThreshold)/10);
    	// set text view to display X Y and Z values using setText and math.round to round values
    	//mTextView1.setText("x: " + Math.round(CurrX) + ";\n y:" + Math.round(CurrY) +";\n z:" + Math.round(CurrZ));
    	//mTextView1.setText("x: " + x + ";\n y:" + y +";\n z:" + z);
    	
    	//mTextView2.setText("%-X: " + PercentX + ";\n %-Y:" + PercentY +";\n %-Z:" + PercentZ);
    	// update values
    	mSensorX = x;
	    mSensorY = y;
	    mSensorZ = z;
	    
	    //drawView.invalidate();
	    //=============================== display data on display==================
    }
    
    private final SensorEventListener mySensorListener = new SensorEventListener() {
    	public void onSensorChanged(SensorEvent event) {
    		ProcessData(event.values[0], event.values[1], event.values[2]);
    	}
    	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };
    
    @Override
    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener(mySensorListener, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }
    
    @Override
    protected void onStop(){
        mSensorManager.unregisterListener(mySensorListener);
        super.onStop();
    }
    
    //========For graphics on screen============
    
    public class DrawView extends View {
        Paint paint = new Paint();   
        
        public DrawView(Context context) {
	    	super(context);
	    	paint.setColor(Color.WHITE);
	    	paint.setStrokeWidth(20);
        }
        
        @Override
        public void onDraw(Canvas canvas) {
        		canvas.drawLine(width/2, height/2, width/2-PercentX*10, height/2+PercentY*10, paint);
        		drawView.invalidate();
        }

    }
    
}