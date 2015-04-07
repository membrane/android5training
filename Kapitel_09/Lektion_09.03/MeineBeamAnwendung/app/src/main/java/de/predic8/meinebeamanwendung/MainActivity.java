package de.predic8.meinebeamanwendung;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


public class MainActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback {

    NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
            // keine NFC Unterstützung
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // keine Android Beam Unterstützung
        } else {
            // registrieren, daß unsere Activity "Android Beam" unterstützt
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
            mNfcAdapter.setNdefPushMessageCallback(this, this);
        }

        handleIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent.getExtras() != null) {
            Object messages = intent.getExtras().get(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (messages instanceof Parcelable[])
                // empfange NFC Nachricht behandeln
                for (Object m : (Parcelable[]) messages) {
                    if (!(m instanceof NdefMessage))
                        continue;
                    NdefMessage message = (NdefMessage) m;
                    for (NdefRecord record : message.getRecords()) {
                        if ("application/de.predic8.meinebeamanwendung".equals(new String(record.getType()))) {
                            String text = new String(record.getPayload());
                            ((EditText) findViewById(R.id.editText)).setText(text);
                        }
                    }
                }
        }

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
    public NdefMessage createNdefMessage(NfcEvent event) {
        // NdefMessage konstruieren, die unsere Activity repräsentiert

        String text = ((EditText) findViewById(R.id.editText)).getText().toString();

        return new NdefMessage(
                new NdefRecord[] {
                        // Teil 1: Text
                        NdefRecord.createMime("application/de.predic8.meinebeamanwendung", text.getBytes()),
                        // Teil 2 (falls Teil 1 vom Empfänger nicht verstanden wird): Referenz auf unsere App
                        NdefRecord.createApplicationRecord("de.predic8.meinebeamanwendung")
                });
    }
}
