package de.predic8.meineinappbillingdemo;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends Activity implements ServiceConnection {

    private static final int REQUEST_CODE_BUY = 1;

    IInAppBillingService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((Button)findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // TODO: nicht im main thread aufrufen
                    ArrayList<String> skuList = new ArrayList<String>();
                    skuList.add("my_item");
                    Bundle querySkus = new Bundle();
                    querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
                    Bundle skuDetails = mService.getSkuDetails(3, getPackageName(), "inapp", querySkus);

                    int response = skuDetails.getInt("RESPONSE_CODE");
                    if (response == 0) {
                        ArrayList<String> responseList
                                = skuDetails.getStringArrayList("DETAILS_LIST");

                        for (String thisResponse : responseList) {
                            JSONObject object = new JSONObject(thisResponse);
                            String sku = object.getString("productId");
                            String price = object.getString("price");
                            Log.d("MainActivity", "Preis: " + price);
                        }
                    }

                    Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
                            "my_item", "inapp", "additional_arguments" /* TODO: Token verwenden */);

                    response = skuDetails.getInt("RESPONSE_CODE");
                    if (response == 0) {
                        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

                        startIntentSenderForResult(pendingIntent.getIntentSender(),
                                REQUEST_CODE_BUY, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                                Integer.valueOf(0));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        });

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_BUY) {
            TextView tv = (TextView)findViewById(R.id.textView);
            if (resultCode == RESULT_OK) {
                String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

                tv.setText("Gekauft:\n" + purchaseData + "\n" + dataSignature);
            } else {
                tv.setText("Nicht gekauft!");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null)
            unbindService(this);
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
    public void onServiceDisconnected(ComponentName name) {
        mService = null;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mService = IInAppBillingService.Stub.asInterface(service);

        try {
            int isSupported  = mService.isBillingSupported(3, getPackageName(), "inapp");

            TextView tv = (TextView)findViewById(R.id.textView);
            tv.setText("isSupported = " + isSupported);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
