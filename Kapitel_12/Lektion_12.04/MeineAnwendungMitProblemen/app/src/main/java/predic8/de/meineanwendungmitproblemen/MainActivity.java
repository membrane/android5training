package predic8.de.meineanwendungmitproblemen;

import android.app.Activity;
import android.app.ListActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.HashMap;


public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 1000;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                try {
                    View item = getLayoutInflater().inflate(R.layout.item, null, false);

                    TextView tv = (TextView) item.findViewById(R.id.textView);
                    TextView tv2 = (TextView) item.findViewById(R.id.textView2);
                    ImageView iv = (ImageView) item.findViewById(R.id.imageView);

                    byte[] data = IOUtils.toByteArray(getResources().getAssets().open("tiger.jpg"));

                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                    tv.setText("Tiger");
                    tv2.setText("eine Großkatze");
                    iv.setImageBitmap(bitmap);

                    return item;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
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
