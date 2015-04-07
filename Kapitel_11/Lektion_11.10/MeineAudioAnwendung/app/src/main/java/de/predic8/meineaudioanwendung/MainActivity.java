package de.predic8.meineaudioanwendung;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.io.File;
import java.io.IOException;


public class MainActivity extends Activity {

    ImageButton ibRecord, ibPlay;

    MediaRecorder mr;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ibRecord = (ImageButton)findViewById(R.id.ibRecord);
        ibPlay = (ImageButton)findViewById(R.id.ibPlay);
        setupClickListeners();
    }

    private void setupClickListeners() {
        File f = new File(Environment.getExternalStorageDirectory(), "myrecording.3gp");
        final String file = f.getAbsolutePath();
        System.out.println(file);

        ibRecord.setEnabled(true);
        ibPlay.setEnabled(f.exists());

        ibRecord.setImageResource(R.drawable.record);
        ibRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mr = new MediaRecorder();
                mr.reset();
                mr.setAudioSource(MediaRecorder.AudioSource.MIC);
                mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mr.setOutputFile(file);
                try {
                    mr.prepare();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                mr.start();
                ibRecord.setImageResource(R.drawable.stop);
                ibPlay.setEnabled(false);

                ibRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mr.stop();
                        mr.reset();
                        mr.release();
                        setupClickListeners();
                    }
                });
            }
        });

        ibPlay.setImageResource(R.drawable.play);
        ibPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp = new MediaPlayer();
                mp.reset();
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.setVolume(1.0f, 1.0f);
                try {
                    mp.setDataSource(file);
                    mp.prepare();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                mp.start();
                ibRecord.setEnabled(false);
                ibPlay.setImageResource(R.drawable.stop);
                ibPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mp.stop();
                        mp.reset();
                        mp.release();
                        setupClickListeners();
                    }
                });
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaPlayer.reset();
                        mediaPlayer.release();
                        setupClickListeners();
                    }
                });
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
