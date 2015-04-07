package de.predic8.meinenewsfeedanwendung;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class GcmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        System.out.println("received push:");
        for (String key : extras.keySet())
            System.out.println(key + " = " + extras.get(key).toString());

        setResultCode(Activity.RESULT_OK);
    }
}
