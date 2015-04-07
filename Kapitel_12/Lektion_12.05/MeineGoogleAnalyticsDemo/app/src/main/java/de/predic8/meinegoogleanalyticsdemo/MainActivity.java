package de.predic8.meinegoogleanalyticsdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.analytics.HitBuilders;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((Button)findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyApplication)getApplication()).getTracker().send(new HitBuilders.EventBuilder()
                        .setCategory("User Flow")
                        .setAction("Start Activity2")
                        .setLabel("via button")
                        .setValue(1)
                        .build());

                startActivity(new Intent(MainActivity.this, Activity2.class));
            }
        });
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
        if (id == R.id.launch_activity2) {
            ((MyApplication)getApplication()).getTracker().send(new HitBuilders.EventBuilder()
                    .setCategory("User Flow")
                    .setAction("Start Activity2")
                    .setLabel("via menu")
                    .setValue(1)
                    .build());

            startActivity(new Intent(this, Activity2.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
