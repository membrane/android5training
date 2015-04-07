package de.predic8.meinerenderscriptdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;


public class MainActivity extends Activity {

    SeekBar sb;
    ImageView iv;
    ScriptIntrinsicBlur theIntrinsic;
    Bitmap destination;
    Allocation tmpOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sb = (SeekBar)findViewById(R.id.seekBar);
        iv = (ImageView) findViewById(R.id.imageView);

        Bitmap source = BitmapFactory.decodeResource(getResources(),
                R.drawable.tiger);

        destination = Bitmap.createBitmap(source);

        RenderScript rs = RenderScript.create(this);
        theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));;
        Allocation tmpIn = Allocation.createFromBitmap(rs, source);
        tmpOut = Allocation.createFromBitmap(rs, destination);
        theIntrinsic.setInput(tmpIn);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void apply() {
        float radius = sb.getProgress() / 4.0f;
        if (radius == 0)
            radius = 0.0001f;

        theIntrinsic.setRadius(radius);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(destination);
        iv.setImageBitmap(destination);
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
