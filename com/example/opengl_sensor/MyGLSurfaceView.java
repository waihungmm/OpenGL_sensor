package com.example.opengl_sensor;

import android.content.Context;
import android.opengl.GLSurfaceView;

class MyGLSurfaceView extends GLSurfaceView {

    private final CubeRenderer myRenderer;

    private float v_x, v_y, v_z;

    public MyGLSurfaceView(Context context){

        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        myRenderer = new CubeRenderer();

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(myRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        v_x = 0f;
        v_y = 0f;
        v_z = 0f;
    } // MyGLSurfaceView

    public void update_rotation (float[] R, boolean walk) {
        // since the rotation matrix multiplies unit vector (z=1) is equal to R13, R23, R33)
        // openGL is column major
        // transpose and becomes 2, 6, 10
        if (walk) {
            v_x = R[2];
            v_y = R[6];
            v_z = R[10];
        } else {
            v_x = 0f;
            v_y = 0f;
            v_z = 0f;
        }
        myRenderer.update_rotation_matrix(R, v_x, v_y, v_z);
        requestRender();
    } // update_rotation

} // class MyGLSurfaceView