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


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startFeedUpdate();
    }

    private void startFeedUpdate() {
        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... voids) {
                try {
                    HttpClient hc = new DefaultHttpClient();
                    HttpGet get = new HttpGet("http://www.heise.de/newsticker/heise-atom.xml");
                    HttpResponse res = hc.execute(get);
                    try {
                        if (res.getStatusLine().getStatusCode() == 200) {
                            XmlPullParser xpp = Xml.newPullParser();
                            xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                            xpp.setInput(res.getEntity().getContent(), null);

                            ArrayList<String> result = new ArrayList<>();

                            while (xpp.next() != XmlPullParser.END_DOCUMENT) {
                                if (xpp.getEventType() == XmlPullParser.START_TAG && xpp.getName().equals("title")) {
                                    if (xpp.next() == XmlPullParser.TEXT)
                                        result.add(xpp.getText());
                                }
                            }

                            return result;
                        }
                    } finally {
                        res.getEntity().consumeContent();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<String> entries) {
                if (entries == null)
                    return;
                ListView lv = (ListView)findViewById(R.id.list_view);
                lv.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, entries));
            }
        }.execute();
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
