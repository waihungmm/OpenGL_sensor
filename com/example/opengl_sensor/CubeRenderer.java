package com.example.opengl_sensor;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

public class CubeRenderer implements GLSurfaceView.Renderer {

    // private Triangle mTriangle;
    private Cube mCube;

    private float x_origin;
    private float y_origin;
    private float z_origin;

    float[] sensor_rotation_matrix = new float[16];
    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        mCube = new Cube();
        // Set the background frame color
        GLES20.glClearColor(0.3f, 0.3f, 0.9f, 1.0f);
        x_origin = 0f;
        y_origin = 0f;
        z_origin = 0f;
    } // onSurfaceCreated


    public void onDrawFrame(GL10 unused) {
        float[] modelMatrix = new float[16];
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Matrix.setLookAtM(viewMatrix, 0, x_origin, y_origin, z_origin, 0f, 0f, 10f, 0f, 1f, 0f);
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 0, 0f, 0f, 1f, 0f, 1f, 0f);
        // eye is eye coordinates
        // center is the look-through center axis
        // up is the screen up axis
        // Set the camera position (View matrix) based on the sensor rotation matrix
        // Matrix.multiplyMM(viewMatrix, 0, sensor_rotation_matrix, 0, viewMatrix, 0);

        final float w = 6f;


        // first club
        // translate and then rotate
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, w-x_origin, w-y_origin, w-z_origin);
        Matrix.multiplyMM(modelMatrix, 0, sensor_rotation_matrix, 0, modelMatrix, 0);

        Matrix.multiplyMM(vPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, vPMatrix, 0);
        // mTriangle.draw(scratch);
        mCube.draw(vPMatrix);

        // draw 2nd cube
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, w-x_origin, -w-y_origin, w-z_origin);
        Matrix.multiplyMM(modelMatrix, 0, sensor_rotation_matrix, 0, modelMatrix, 0);

        Matrix.multiplyMM(vPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, vPMatrix, 0);
        mCube.draw(vPMatrix);

        // draw 3rd
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, -w-x_origin, w-y_origin, w-z_origin);
        Matrix.multiplyMM(modelMatrix, 0, sensor_rotation_matrix, 0, modelMatrix, 0);

        Matrix.multiplyMM(vPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, vPMatrix, 0);
        mCube.draw(vPMatrix);

        // draw 4th
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, -w-x_origin, -w-y_origin, w-z_origin);
        Matrix.multiplyMM(modelMatrix, 0, sensor_rotation_matrix, 0, modelMatrix, 0);

        Matrix.multiplyMM(vPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, vPMatrix, 0);
        mCube.draw(vPMatrix);

        // draw 5th
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, w-x_origin, w-y_origin, -w-z_origin);
        Matrix.multiplyMM(modelMatrix, 0, sensor_rotation_matrix, 0, modelMatrix, 0);

        Matrix.multiplyMM(vPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, vPMatrix, 0);
        mCube.draw(vPMatrix);

        // draw 6th
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, w-x_origin, -w-y_origin, -w-z_origin);
        Matrix.multiplyMM(modelMatrix, 0, sensor_rotation_matrix, 0, modelMatrix, 0);

        Matrix.multiplyMM(vPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, vPMatrix, 0);
        mCube.draw(vPMatrix);

        // draw 7th
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, -w-x_origin, w-y_origin, -w-z_origin);
        Matrix.multiplyMM(modelMatrix, 0, sensor_rotation_matrix, 0, modelMatrix, 0);

        Matrix.multiplyMM(vPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, vPMatrix, 0);
        mCube.draw(vPMatrix);

        // draw 8th
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, -w-x_origin, -w-y_origin, -w-z_origin);
        Matrix.multiplyMM(modelMatrix, 0, sensor_rotation_matrix, 0, modelMatrix, 0);

        Matrix.multiplyMM(vPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, vPMatrix, 0);
        mCube.draw(vPMatrix);

    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates in the onDrawFrame() method
        // interpretation :  z=-near and z=-far
        // To keep the aspect ratio, ratio should be equal to (right-left) / (top-bottom)
        // near is the focal length (sort of)
        // the original example as bug
        if (width > height)
          Matrix.frustumM(projectionMatrix, 0, -ratio * 2, ratio * 2, -2, 2, 1, 10);
        else
          Matrix.frustumM(projectionMatrix, 0, -2, 2, -2 / ratio, 2 / ratio, 1, 20);
    } // onSurfaceChanged

    public static int loadShader(int type, String shaderCode){

        // Shader calls should be within a GL thread that is
        // onSurfaceChanged(), onSurfaceCreated() or onDrawFrame()

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    } // loadShader

    public void  update_rotation_matrix (float[] R, float dx, float dy, float dz)
    {
      sensor_rotation_matrix = R.clone();
        x_origin += dx;
        y_origin += dy;
        z_origin += dz;
    } // update_rotation_matrix



}
