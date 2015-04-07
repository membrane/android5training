package de.predic8.meinebinderdemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity implements ServiceConnection {

    MyDemoService.MyBinder binder;

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binder.computeNextPrime(new MyDemoService.OnPrimeComputedHandler() {
                    @Override
                    public void onPrimeComputed(Long nextPrime) {
                        ((TextView)findViewById(R.id.textView)).setText("Primzahl: " + nextPrime);
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        bindService(new Intent(this, MyDemoService.class), this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        unbindService(this);

        super.onStop();
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

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        button.setEnabled(true);
        binder = (MyDemoService.MyBinder) service;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        button.setEnabled(false);
        binder = null;
    }
}
