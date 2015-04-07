package de.predic8.meinecontentresolveranwendung;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((Button)findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = getContentResolver().query(Uri.parse("content://de.predic8.mydata"),
                        null, null, null, null);

                if (cursor.moveToFirst()) {
                    do {
                        for (int i = 0; i < cursor.getColumnCount(); i++) {
                            StringBuilder line = new StringBuilder();
                            line.append(cursor.getColumnName(i) + " = ");
                            switch (cursor.getType(i)) {
                                case Cursor.FIELD_TYPE_NULL:
                                    line.append("null");
                                    break;
                                case Cursor.FIELD_TYPE_INTEGER:
                                    line.append(cursor.getInt(i));
                                    break;
                                case Cursor.FIELD_TYPE_STRING:
                                    line.append(cursor.getString(i));
                                    break;
                                default:
                                    line.append("not implemented: type " + cursor.getType(i));
                                    break;
                            }
                            System.out.println(line.toString());

                        }
                    } while (cursor.moveToNext());
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
