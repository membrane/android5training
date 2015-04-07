package de.predic8.meinenewsfeedanwendung;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Xml;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

public class FeedDownloaderService extends Service {
    public FeedDownloaderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startFeedUpdate();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
                System.out.println("download completed");
                stopSelf();
                //ListView lv = (ListView)findViewById(R.id.list_view);
                //lv.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, entries));
            }
        }.execute();
    }

}
