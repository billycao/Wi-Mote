
package accel.test;
/*
 *Saqib's Accelerometer test
 */


import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;


public class Accel_testActivity extends Activity   {

	private SensorManager mSensorManager;
	private List<Sensor> sensors;
	private Sensor mAccelerometer;
    
    private float mSensorX, mSensorY, mSensorZ = 0f;
    private float mMaxX, mMaxY, mMaxZ = 0f;

    private TextView mTextView1;
    private TextView mTextView2;
    

    /** Called when the activity is first created. */
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accel_test);
        
        mTextView1 = (TextView)findViewById(R.id.textView1);
        mTextView2 = (TextView)findViewById(R.id.textView2);
        
        
        //set the sensors and manager
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(sensors.size() >0)
        {
        	mAccelerometer = sensors.get(0);
        }
        
        
    }
    
    private void updateView(float x, float y, float z)
    {
    	// adjust values to display on screen. 
    	float CurrX = x - mSensorX *10;
    	float CurrY = y - mSensorY *10;
    	float CurrZ = z - mSensorZ *10;
    
    	// set text view to display X Y and Z values using setText and math.round to round values
    	mTextView1.setText("x: " + Math.round(CurrX) + ";\n y:" + Math.round(CurrY) +";\n z:" + Math.round(CurrZ));
    	//mTextView1.setText("x: " + CurrX + ";\n y:" + CurrY +";\n z:" + CurrZ);
    		
    	mMaxX = Math.round(Math.max(mMaxX, CurrX));
    	mMaxY = Math.round(Math.max(mMaxY, CurrY));
    	mMaxZ = Math.round(Math.max(mMaxZ, CurrZ));
    			
    	mTextView2.setText("MaxX: " + mMaxX + ";\n MaxY:" + mMaxY +";\n MaxZ:" + mMaxZ);
    	
    	// update values
    	mSensorX = x;
	    mSensorY = y;
	    mSensorZ = z;
    
    }
    
    private final SensorEventListener mySensorListener = new SensorEventListener()
    {
    	public void onSensorChanged(SensorEvent event)
    	{
    		updateView(event.values[0], event.values[1], event.values[2]);
    	}
    	
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };
    

    
    @Override
    protected void onResume()
    {
        super.onResume();
        mSensorManager.registerListener(mySensorListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    @Override
    protected void onStop() 
    {
        mSensorManager.unregisterListener(mySensorListener);
        super.onStop();
    }
    
    


    
}