package org.terasology.gestalt.example.asteroids.android;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView glView;
    private AndroidAsteroids game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glView = new GLSurfaceView(this);
        glView.setEGLContextClientVersion(2);
        glView.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                game = new AndroidAsteroids(MainActivity.this.getResources());
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                game.reshape(width, height);
                GLES20.glViewport(0,0,width, height);
            }

            @Override
            public void onDrawFrame(GL10 gl) {
                float sin = (float) Math.sin((System.currentTimeMillis() % 1000000) / 1000.0f);
                GLES20.glClearColor(0.2f, 0.4f, sin * sin, 1.0f);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                game.draw();
                game.idle();
            }
        });
        setContentView(glView);
    }
}
