package com.because.wimote;
/*
 *Saqib's Accelerometer test
 */

import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * TODO jdoc it? 
 *
 */
public class AccelActivity extends Activity  implements OnClickListener  {
	private WiMoteUtil util;
	
	private SensorManager mSensorManager;
	private List<Sensor> sensors;
	private Sensor mAccelerometer;
	SharedPreferences.Editor pref_editor; 
	
  //  private TextView mTextView1, mTextView2; // TODO remove test output
    private TextView mSensitivity, mThreshold;
    ToggleButton toggleRun;
    
    //For Accel________________________
	float Accel_Sensitivity_Max = (float) 10;//Float.parseFloat(accel_settings.getString("aSensitivity", "4.0f"));
	float AccelMaximum = (float) 10;
	float DetectionThreshold = (float) 2; //Float.parseFloat(accel_settings.getString("aThreshold", "2.0f"));
	int MouseSensitivityPercent = 50;
	
    //for server________________________________________
    String szAccelPercent = new String();

    /** Called when the activity is first created. */
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accel);
        
        util = new WiMoteUtil(this);
        
        SharedPreferences accel_settings = PreferenceManager.getDefaultSharedPreferences(this);
    	pref_editor = accel_settings.edit();
    	
    	MouseSensitivityPercent = accel_settings.getInt("aMouseSensitivity", 50);
    	//Accel_Sensitivity_Max = (float) Float.parseFloat(accel_settings.getString("aSensitivity", "4.0f"));
    	AccelMaximum = 10f;
    	DetectionThreshold = (float) Float.parseFloat(accel_settings.getString("aThreshold", "2.0f"));
    	
        //testing output TODO: remove test
       
        //mTextView1 = (TextView)findViewById(R.id.Accel_textView1);
        //TextView2 = (TextView)findViewById(R.id.Accel_textView2);
        
        mSensitivity = (TextView) findViewById(R.id.Accel_sens_text);
        mThreshold = (TextView) findViewById(R.id.Accel_thres_text);
        
        
        Button buttonSensUp = (Button) findViewById(R.id.Accel_sens_button_up);
        buttonSensUp.setOnClickListener(this);
        Button buttonSensDn = (Button) findViewById(R.id.Accel_sens_button_down);
        buttonSensDn.setOnClickListener(this);
        Button buttonThresUp = (Button) findViewById(R.id.Accel_thres_button_up);
        buttonThresUp.setOnClickListener(this);
        Button buttonThresDn = (Button) findViewById(R.id.Accel_thres_button_down);
        buttonThresDn.setOnClickListener(this);
        
        //((Button)findViewById(R.id.LeftClick)).setOnTouchListener(buttonListener);
        //((Button)findViewById(R.id.RightClick)).setOnTouchListener(buttonListener);

        toggleRun = (ToggleButton)findViewById(R.id.Accel_toggleButton1);
        toggleRun.setChecked(true);
    	
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
    //-------------------------------------------------------------------  
  //+++++++++++++++++++BUTTON+++++++++++++++++++++++++++++++++++++++++++++++++
  	public void onClick(View v) {
  		switch (v.getId()) {
  		case R.id.Accel_sens_button_up:
  			MouseSensitivityPercent = util.ChangeSensitivity(2);
  			break;
  		case R.id.Accel_sens_button_down:
  			MouseSensitivityPercent = util.ChangeSensitivity(-2);
  			break;
  		case R.id.Accel_thres_button_up:
  			DetectionThreshold = util.ChangeThreshold(.2f);
  			break;
  		case R.id.Accel_thres_button_down:
  			DetectionThreshold = util.ChangeThreshold(-.2f);
  			break;
  		}
  		//Accel_Sensitivity_Max = (float) Math.rint(10*Accel_Sensitivity_Max)/10;
  		DetectionThreshold = (float) Math.rint(10*DetectionThreshold)/10;
  	}
  	//+++++++++++++++++++BUTTON+++++++++++++++++++++++++++++++++++++++++++++++++
	
    private void ProcessData(float x, float y, float z) {
    	float[] delta = new float[2];
    	delta = util.processAccel(x, y, z);

    	//---------------Pass Accelerometer data to Network Function------------------ 	
    	//int RoundX =  -Math.round(PercentX);
    	//int RoundY =  Math.round(PercentY);
 
    //	szAccelPercent = Integer.toString(RoundX, 10) + " " + Integer.toString(RoundY, 10);
    	szAccelPercent = Float.toString(delta[0]) + " " + Float.toString(delta[1]);
    	
    //	mTextView2.setText("ACCEL " + szAccelPercent);  // test output
    	if(toggleRun.isChecked())
    		util.new UDPHelper().execute("MOUSE_DELTA " + szAccelPercent);

    	//-------------Server Passing complete---------------------------------------------
    	
    	//===================== display data on view=============================
    	// adjust values to display on screen. 
    	/*
    	float CurrX = x - mSensorX *10;
    	float CurrY = y - mSensorY *10;
    	float CurrZ = z - mSensorZ *10;
    */
    	
    //	mSensitivity.setText("" + Math.rint(10*Accel_Sensitivity_Max)/10);
    	mSensitivity.setText("" + MouseSensitivityPercent + "%");
    	mThreshold.setText("" + Math.rint(10*DetectionThreshold)/10);
    	
	    //=============================== display data on display==================

    	try {
    		Thread.sleep(0, 10000);
    	} catch (InterruptedException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
		}
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
    protected void onPause(){
        mSensorManager.unregisterListener(mySensorListener);
      //  pref_editor.putString("aSensitivity", Float.toString(Accel_Sensitivity_Max));
        pref_editor.putInt("aMouseSensitivity", MouseSensitivityPercent);
		pref_editor.putString("aThreshold", Float.toString(DetectionThreshold));
		pref_editor.commit();
        super.onPause();
    }
    
    //========For graphics on screen============
    /*
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
    */
    
}