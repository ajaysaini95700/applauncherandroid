package notes.com.azri.applauncher;

        import android.app.Activity;
        import android.content.Context;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.os.Bundle;
        import android.os.Vibrator;
        import android.widget.TextView;
        import android.widget.Toast;

public class SensorActivity extends Activity implements SensorEventListener {

    private float lastX, lastY, lastZ;
    TextToSpeach tts;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private float predeltaX = 0;
    private float predeltaY = 0;
    private float predeltaZ = 0;
    private TextView currentX, currentY, currentZ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor);
        tts = new TextToSpeach(getApplicationContext());
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // fai! we dont have an accelerometer!
            Toast.makeText(getApplicationContext() , "dont have an accelerometer " ,Toast.LENGTH_SHORT).show();
        }

    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // display the max x,y,z accelerometer values

        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.round(Math.abs(lastX - event.values[0]));
        deltaY = Math.round(Math.abs(lastY - event.values[1]));
        deltaZ = Math.round(Math.abs(lastZ - event.values[2]));

       if( deltaX !=predeltaX && deltaY !=predeltaY && deltaZ !=predeltaZ)
       {
           if (deltaX < 1)
               deltaX = 0;
           if (deltaY < 1)
               deltaY = 0;
           if (deltaZ < 1)
               deltaZ = 0;
           predeltaX = deltaX;
           predeltaY = deltaY;
           predeltaZ = deltaZ;
           // display the current x,y,z accelerometer values
           displayCurrentValues();
       }

    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        if(deltaX > 0 && deltaX > 0 && deltaZ > 0) {
            tts.speach("Do not touch mobile phone");
            currentX.setText(Float.toString(deltaX));
            currentY.setText(Float.toString(deltaY));
            currentZ.setText(Float.toString(deltaZ));
        }
    }
}

