package de.predic8.meineopenglanwendung;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MyGLSurfaceView extends GLSurfaceView {

    private MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context){
        super(context);

        init();
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs){
        super(context, attrs);

        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);

        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
