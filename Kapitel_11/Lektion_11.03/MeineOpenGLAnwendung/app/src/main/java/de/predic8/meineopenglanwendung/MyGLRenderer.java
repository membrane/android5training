package de.predic8.meineopenglanwendung;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {


        class Triangle {

            private FloatBuffer vertexBuffer;

            static final int COORDS_PER_VERTEX = 3;
            float triangleCoords[] = {
                    0.0f,  0.622008459f, 0.0f,
                    -0.5f, -0.311004243f, 0.0f,
                    0.5f, -0.311004243f, 0.0f
            };

            float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

            private final String vertexShaderCode =
                    "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

            private final String fragmentShaderCode =
                    "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor * dot(normalize(gl_FragCoord.xyz), vec3(1.0, 1.0, 1.0));" +
                    "}";

            private final int mProgram;

            private int mMVPMatrixHandle;

            public Triangle() {

                ByteBuffer bb = ByteBuffer.allocateDirect(
                        triangleCoords.length * 4);
                bb.order(ByteOrder.nativeOrder());

                vertexBuffer = bb.asFloatBuffer();
                vertexBuffer.put(triangleCoords);
                vertexBuffer.position(0);

                int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                        vertexShaderCode);
                int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                        fragmentShaderCode);

                mProgram = GLES20.glCreateProgram();

                GLES20.glAttachShader(mProgram, vertexShader);

                GLES20.glAttachShader(mProgram, fragmentShader);

                GLES20.glLinkProgram(mProgram);
            }

            private int mPositionHandle;
            private int mColorHandle;

            private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
            private final int vertexStride = COORDS_PER_VERTEX * 4;

            public void draw(float[] mvpMatrix) {
                GLES20.glUseProgram(mProgram);

                mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

                GLES20.glEnableVertexAttribArray(mPositionHandle);

                GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                        GLES20.GL_FLOAT, false,
                        vertexStride, vertexBuffer);

                mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

                GLES20.glUniform4fv(mColorHandle, 1, color, 0);

                mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

                GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

                GLES20.glDisableVertexAttribArray(mPositionHandle);
            }
        }

    public int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        if (compileStatus[0] == 0)
            throw new RuntimeException(GLES20.glGetShaderInfoLog(shader));

        return shader;
    }

    Triangle mTriangle;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float[] mRotationMatrix = new float[16];
    private float[] scratch = new float[16];

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        mTriangle = new Triangle();

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // zeitbasierte Drehung
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);

        Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 0, -1.0f);

        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        mTriangle.draw(scratch);
    }

}
