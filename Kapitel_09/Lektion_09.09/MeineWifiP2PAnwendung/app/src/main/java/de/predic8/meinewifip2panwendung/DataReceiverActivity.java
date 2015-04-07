package de.predic8.meinewifip2panwendung;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class DataReceiverActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_receiver);

        openSocket();
    }

    @Override
    protected void onDestroy() {
        closeSocket();

        super.onDestroy();
    }

    ServerSocket serverSocket;
    Thread threadDataReceiver;

    private void openSocket() {
        try {
            serverSocket = new ServerSocket(9876);
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
        threadDataReceiver = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Socket s = serverSocket.accept();
                        System.out.println("got connection");
                        ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

                        while (true) {
                            byte[] frame = (byte[]) ois.readObject();
                            System.out.println("received " + frame.length + " bytes");
                            final Bitmap b = BitmapFactory.decodeByteArray(frame, 0, frame.length);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((ImageView)findViewById(R.id.imageView)).setImageBitmap(b);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }
            }
        };
        threadDataReceiver.start();
    }

    private void closeSocket() {
        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        threadDataReceiver.interrupt();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data_receiver, menu);
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
