package de.predic8.meinenewsfeedanwendung;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.AsyncTaskLoader;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements EventBus.IEventListener, LoaderManager.LoaderCallbacks<List<String>> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                30 * 1000,
                30 * 1000,
                PendingIntent.getService(this, 0, new Intent(this, FeedDownloaderService.class), 0));

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.register(this);
        onFeedUpdate();
    }

    @Override
    protected void onStop() {
        EventBus.unregister(this);
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

        if (id == R.id.action_start_service) {
            startService(new Intent(this, FeedDownloaderService.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFeedUpdate() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<List<String>> onCreateLoader(int i, Bundle bundle) {
        return new AsyncTaskLoader<List<String>>(this) {

            {
                onContentChanged();
            }

            @Override
            protected void onStartLoading() {
                if (takeContentChanged())
                    forceLoad();
            }

            @Override
            public List<String> loadInBackground() {
                return new MyStorage(getContext()).load();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<String>> listLoader, List<String> strings) {
        ListView lv = (ListView) findViewById(R.id.list_view);
        lv.setAdapter(new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, strings));
    }

    @Override
    public void onLoaderReset(Loader<List<String>> listLoader) {
        ListView lv = (ListView) findViewById(R.id.list_view);
        lv.setAdapter(null);
    }
}
