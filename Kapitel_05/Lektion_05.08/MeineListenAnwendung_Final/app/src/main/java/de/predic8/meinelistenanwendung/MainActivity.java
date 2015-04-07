package de.predic8.meinelistenanwendung;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lv = (ListView) findViewById(R.id.list_view);

        lv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return Note.getNoten().size();
            }

            @Override
            public Object getItem(int i) {
                return Note.getNoten().get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                View meineZeile = view;
                if (meineZeile == null)
                    meineZeile = getLayoutInflater().inflate(R.layout.meine_zeile, viewGroup, false);

                Note note = Note.getNoten().get(i);

                ImageView iv = (ImageView) meineZeile.findViewById(R.id.meine_zeile_bild);
                iv.setImageResource(note.grafik);

                TextView tv = (TextView) meineZeile.findViewById(R.id.meine_zeile_text);
                tv.setText(note.text);

                return meineZeile;
            }
        });
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
