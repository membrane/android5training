package de.predic8.meinesensoranwendung;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends Activity {

    SensorManager sm;

    Sensor rotationSensor, accelerometerSensor;
    SensorEventListener rotationListener, accelerometerListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationSensor = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        accelerometerSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        rotationListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                StringBuilder sb = new StringBuilder();
                for (float v : event.values) {
                    sb.append(v);
                    sb.append("\n");
                }
                ((TextView)findViewById(R.id.text)).setText(sb.toString());
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        accelerometerListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                StringBuilder sb = new StringBuilder();
                for (float v : event.values) {
                    sb.append(v);
                    sb.append("\n");
                }
                ((TextView)findViewById(R.id.text2)).setText(sb.toString());
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    protected void onResume() {
        super.onResume();
        sm.registerListener(rotationListener, rotationSensor, SensorManager.SENSOR_DELAY_UI);
        sm.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        sm.unregisterListener(rotationListener);
        sm.unregisterListener(accelerometerListener);
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
