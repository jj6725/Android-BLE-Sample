package com.dable.glrender;

/**
 * Created by user on 16/6/8.
 */
import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MyGLSurfaceView extends GLSurfaceView {

    private static final String TAG = "MyGLRenderer";

    private final MyGLRenderer mRenderer;

    Intent intent;

    public MyGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }



    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //init(context);
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }


   // private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
   // private float mPreviousX;
   // private float mPreviousY;

    float min = 0;
    float max = 0.5f;

    float mx;
    float my;
    float mz;
    float mAngle;




    public float getAngle() {
        return mAngle;
    }

    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    public void setAngle(float angle) {
        mAngle = angle;
    }

    public float getx(){
        return mx;
    }
    public void setx(float x){
        mx = x;
    }
    public float gety(){
        return my;
    }
    public void sety(float y){
        my = y;
    }
    public float getz(){
        return mz;
    }
    public void setz(float z){
        mz = z;
    }

    public MyGLRenderer getRender(){
        return this.mRenderer;
    }












}
