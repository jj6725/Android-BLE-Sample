package com.dable.glrender;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.dable.cubes.*;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MyGLRenderer";
    //private Triangle mTriangle;
    private Square mSquare;
    private Cube mCube;
    private Cube2 mCube2;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];
    private final float[] mRotationMatrix_ = new float[16];

    //private static final float FRAME_TIME_MILLIS = TimeUnit.SECONDS.toMillis(1) / REFRESH_RATE_FPS;


    private float mAngle;
    private float mx = 0;
    private float my = 0;
    private float mz = 1f;

    private float [][] samples = {
            {1.0f, 1.0f, 0,0},
            {2.0f, 0, 1.0f, 0},
            {3.0f,0,0,1.0f},
            {1.0f, 1.0f, 0,0},
            {2.0f, 0, 1.0f, 0},
            {3.0f,0,0,1.0f},
            {1.0f, 1.0f, 0,0},
            {2.0f, 0, 1.0f, 0},
            {3.0f,0,0,1.0f},
            {1.0f, 1.0f, 0,0},
            {2.0f, 0, 1.0f,0},
            {3.0f,0,0,1.0f},
    };

    private float [] angles = {3,4,5,6,7,8,9,0,3,2,2,4,5,7,3,2,2,1,1,1,3,5,6,3,6};


    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        //mTriangle = new Triangle();
        mSquare   = new Square();
        mCube = new Cube();
        mCube2 = new Cube2();
    }

    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];
        float[] scratch_ = new float[16];

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);


        //Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, 1.0f);
        Matrix.setRotateM(mRotationMatrix, 0, mAngle, mx, my, mz);


        //Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        mCube2.draw(scratch);
        //mSquare.draw(scratch);
        //updateCubeRotation();
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

    }

    /**
     * Utility method for compiling a OpenGL shader.
     *
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }


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


/*
    private void updateCubeRotation() {
         int i =0;
         int j = 0;

        if (i < samples.length ) {
            mAngle += samples[i][j];
            //mx += samples[i][j+1];
            //my += samples[i][j+2];
            //mz += samples[i][j+3];
            Log.w(TAG, "TasdssssssssssssdglkkllkkjbjkabSkavsdkhavdkha dcakhgc duasd cak");

        }else{

        }
        i++;
    }
*/
    private void updateCubeRotation() {
        mAngle -= 0.3f;
        Log.w(TAG, "sssssssfghfjhdlgkhslghdoigbodigorbgobgo/////////////////////////////");
    }



}
