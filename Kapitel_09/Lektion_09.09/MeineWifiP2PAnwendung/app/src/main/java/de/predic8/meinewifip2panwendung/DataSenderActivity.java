package de.predic8.meinewifip2panwendung;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Objects;


public class DataSenderActivity extends Activity {

    public static final String ARG_PEER_IP = "peer_ip";

    private Camera camera;

    private final Object syncRoot = new Object();
    private byte[] frame; // guarded by syncRoot

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_sender);

        openSocketConnection();

        try {
            camera = Camera.open();

            camera.setErrorCallback(new Camera.ErrorCallback() {
                public void onError(int error, Camera mcamera) {
                    camera.release();
                    camera = Camera.open();
                    Log.d("Camera died", "error camera: " + error);
                }
            });

            Camera.Size size = chooseSmallest(camera.getParameters().getSupportedPreviewSizes());
            camera.getParameters().setPreviewSize(size.width, size.height);


            camera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    System.out.println("preview frame");

                    Camera.Parameters parameters = camera.getParameters();
                    int width = parameters.getPreviewSize().width;
                    int height = parameters.getPreviewSize().height;

                    YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);

                    byte[] bytes = out.toByteArray();

                    synchronized (syncRoot) {
                        frame = bytes;
                        syncRoot.notify();
                    }
                }
            });

            final SurfaceView sv = new SurfaceView(this) {

            };

            sv.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        camera.setPreviewDisplay(sv.getHolder());
                        camera.startPreview();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("preview started");

                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    System.out.println("surface changed");
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    System.out.println("surface destroyed");
                }
            });

            ((RelativeLayout) findViewById(R.id.relative_layout)).addView(sv);

        } catch (Exception e) {
            finish();
        }

    }

    private Camera.Size chooseSmallest(List<Camera.Size> supportedPreviewSizes) {
        int min = 0;
        for (int i = 1; i < supportedPreviewSizes.size(); i++)
            if (supportedPreviewSizes.get(min).width * supportedPreviewSizes.get(min).height > supportedPreviewSizes.get(i).width * supportedPreviewSizes.get(i).height)
                min = i;
        return supportedPreviewSizes.get(min);
    }

    @Override
    protected void onDestroy() {
        camera.stopPreview();
        camera.release();
        closeSocketConnection();
        super.onDestroy();
    }

    Thread threadDataTransmitter;
    Socket s;

    private void openSocketConnection() {
        final String peer = getIntent().getStringExtra(ARG_PEER_IP);
        threadDataTransmitter = new Thread() {
            @Override
            public void run() {
                try {
                    s = new Socket(peer, 9876);
                    ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                    while (true) {
                        byte[] frameToTransmit;
                        synchronized (syncRoot) {
                            while (true) {
                                frameToTransmit = frame;
                                frame = null;
                                if (frameToTransmit != null)
                                    break;
                                syncRoot.wait();
                            }
                        }

                        System.out.println("sending frame");
                        oos.writeObject(frameToTransmit);
                        oos.flush();
                        oos.reset();
                        System.out.println("frame sent");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }
            }
        };
        threadDataTransmitter.start();
    }

    private void closeSocketConnection() {
        try {
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        threadDataTransmitter.interrupt();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data_sender, menu);
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
