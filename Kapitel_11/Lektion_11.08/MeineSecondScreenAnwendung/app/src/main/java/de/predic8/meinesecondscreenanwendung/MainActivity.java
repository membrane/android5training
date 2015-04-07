package de.predic8.meinesecondscreenanwendung;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.media.MediaControlIntent;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private MediaRouter mMediaRouter;
    private MediaRouteSelector mSelector;
    MyPresentation mPresentation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMediaRouter = MediaRouter.getInstance(this);

        mSelector = new MediaRouteSelector.Builder()
                .addControlCategory(MediaControlIntent.CATEGORY_LIVE_AUDIO)
                .addControlCategory(MediaControlIntent.CATEGORY_LIVE_VIDEO)
                .addControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
                .build();

        ((Button)findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SlideController.getInstance().goToPrevious();
            }
        });
        ((Button)findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SlideController.getInstance().goToNext();
            }
        });
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



    private final MediaRouter.Callback mMediaRouterCallback =
            new MediaRouter.Callback() {

                @Override
                public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo route) {
                    Log.d(TAG, "onRouteSelected: route=" + route);

                    updatePresentation(route);
                }

                @Override
                public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo route) {
                    Log.d(TAG, "onRouteUnselected: route=" + route);

                    updatePresentation(route);
                }

                @Override
                public void onRoutePresentationDisplayChanged(
                        MediaRouter router, MediaRouter.RouteInfo route) {
                    Log.d(TAG, "onRoutePresentationDisplayChanged: route=" + route);

                    updatePresentation(route);
                }
            };

    private void updatePresentation(MediaRouter.RouteInfo route) {
        Display selectedDisplay = null;
        if (route != null) {
            selectedDisplay = route.getPresentationDisplay();
        }

        if (mPresentation != null && mPresentation.getDisplay() != selectedDisplay) {
            mPresentation.dismiss();
            mPresentation = null;
        }

        if (mPresentation == null && selectedDisplay != null) {
            mPresentation = new MyPresentation(this, selectedDisplay);
            mPresentation.setOnDismissListener(
                    new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (dialog == mPresentation) {
                                mPresentation = null;
                            }
                        }
                    });

            try {
                mPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                mPresentation = null; // Verbindung verloren
            }
        }
    }



}
