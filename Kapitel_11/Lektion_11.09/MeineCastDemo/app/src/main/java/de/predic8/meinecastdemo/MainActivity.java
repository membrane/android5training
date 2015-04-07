package de.predic8.meinecastdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaControlIntent;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;

import static com.google.android.gms.cast.Cast.Listener;


public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, ResultCallback<Cast.ApplicationConnectionResult>, Cast.MessageReceivedCallback, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";

    private static final String CAST_APP_ID = "E99D1796";
    private static final String NAMESPACE = "urn:x-cast:de.predic8.meinecastdemo";

    private MediaRouter mMediaRouter;
    private MediaRouteSelector mSelector;

    private GoogleApiClient gac;

    private String sessionId;
    private boolean mApplicationStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMediaRouter = MediaRouter.getInstance(this);

        mSelector = new MediaRouteSelector.Builder()
                .addControlCategory(CastMediaControlIntent.categoryForCast(CAST_APP_ID))
                /*
                .addControlCategory(MediaControlIntent.CATEGORY_LIVE_VIDEO)
                .addControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
                */
                .build();


        ((Button)findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(((EditText)findViewById(R.id.editText)).getText().toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider =
                (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        mediaRouteActionProvider.setRouteSelector(mSelector);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMediaRouter.addCallback(mSelector, mMediaRouterCallback, MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    @Override
    protected void onStop() {
        mMediaRouter.removeCallback(mMediaRouterCallback);
        super.onStop();
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

    private final MediaRouter.Callback mMediaRouterCallback = new MediaRouter.Callback() {

        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo route) {
            Log.d(TAG, "onRouteSelected: route=" + route);
            setStatus("onRouteSelected");

            startCasting(route);
        }

        @Override
        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo route) {
            Log.d(TAG, "onRouteUnselected: route=" + route);
            setStatus("onRouteUnselected");
            teardown();
        }

        @Override
        public void onRoutePresentationDisplayChanged(
                MediaRouter router, MediaRouter.RouteInfo route) {
            Log.d(TAG, "onRoutePresentationDisplayChanged: route=" + route);
            setStatus("onRoutePresentationDisplayChanged");
        }
    };

    Listener mCastClientListener = new Cast.Listener() {
        @Override
        public void onApplicationStatusChanged() {
            if (gac != null) {
                Log.d(TAG, "onApplicationStatusChanged: "
                        + Cast.CastApi.getApplicationStatus(gac));
            }
        }

        @Override
        public void onVolumeChanged() {
            if (gac != null) {
                Log.d(TAG, "onVolumeChanged: " + Cast.CastApi.getVolume(gac));
            }
        }

        @Override
        public void onApplicationDisconnected(int errorCode) {
            teardown();
        }
    };

    private void startCasting(MediaRouter.RouteInfo route) {

        Log.d(TAG, "startCasting: " + route.getId() + " " + route.getDescription());

        CastDevice castDevice = CastDevice.getFromBundle(route.getExtras());
        String routeId = route.getId();

        if (castDevice == null) {
            Log.e(TAG, "castDevice is null");
            return;
        }

        Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions.builder(castDevice, mCastClientListener);

        gac = new GoogleApiClient.Builder(this)
                .addApi(Cast.API, apiOptionsBuilder.build())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        gac.registerConnectionCallbacks(this);
        gac.connect();

    }

    @Override
    public void onConnected(Bundle bundle) {
        setStatus("chromecast connected.");
        Cast.CastApi.launchApplication(gac, CAST_APP_ID, true).setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        teardown();
    }

    @Override
    public void onResult(Cast.ApplicationConnectionResult result) {
        Status status = result.getStatus();
        if (status.isSuccess()) {
            ApplicationMetadata applicationMetadata =
                    result.getApplicationMetadata();
            sessionId = result.getSessionId();
            String applicationStatus = result.getApplicationStatus();
            boolean wasLaunched = result.getWasLaunched();

            mApplicationStarted = true;
            ((Button)findViewById(R.id.button)).setEnabled(true);
            setStatus("application started.");

            try {
                Cast.CastApi.setMessageReceivedCallbacks(gac,
                        NAMESPACE,
                        this);
            } catch (IOException e) {
                Log.e(TAG, "Exception while creating channel", e);
            }
        } else {
            setStatus("failed to start application.");
            teardown();
        }
    }

    @Override
    public void onMessageReceived(CastDevice castDevice, String namespace, String message) {
        Log.d(TAG, "onMessageReceived: " + message);
    }

    private void sendMessage(String message) {
        if (gac != null) {
            try {
                Cast.CastApi.sendMessage(gac, NAMESPACE, message)
                        .setResultCallback(
                                new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(Status result) {
                                        if (!result.isSuccess()) {
                                            Log.e(TAG, "Sending message failed");
                                        }
                                    }
                                });
            } catch (Exception e) {
                Log.e(TAG, "Exception while sending message", e);
            }
        }
    }

    private void teardown() {
        Log.d(TAG, "teardown");
        if (gac != null) {
            if (mApplicationStarted) {
                if (gac.isConnected() || gac.isConnecting()) {
                    try {
                        Cast.CastApi.stopApplication(gac, sessionId);
                        Cast.CastApi.removeMessageReceivedCallbacks(gac, NAMESPACE);
                    } catch (IOException e) {
                        Log.e(TAG, "Exception while removing channel", e);
                    }
                    gac.disconnect();
                }
                mApplicationStarted = false;
                ((Button)findViewById(R.id.button)).setEnabled(false);

            }
            gac = null;
        }
        sessionId = null;
    }

    private void setStatus(String text) {
        ((TextView)findViewById(R.id.text_view)).setText("Zustand: " + text);
    }
}
