package de.predic8.meinecountdownanwendung;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NotificationService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        startForeground(1, new Notification.Builder(this)
                        .setContentTitle("Titel")
                        .setContentText("Text")
                        .addAction(android.R.drawable.ic_media_play, "Open Remote",
                                PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0))
                        .build()
        );
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }
}
