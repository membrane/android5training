package de.predic8.meinenewsfeedanwendung;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
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

public class FeedDownloaderService extends JobService {
    public FeedDownloaderService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        startFeedUpdate(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private void startFeedUpdate(final JobParameters params) {
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
                new MyStorage(FeedDownloaderService.this).store(entries);
                EventBus.fireFeedUpdate();
                jobFinished(params, false);
            }
        }.execute();
    }

}
