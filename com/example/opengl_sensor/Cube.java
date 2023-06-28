package com.example.opengl_sensor;


import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Cube {

    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_DATA_SIZE = 4; // coordinate x, y, z and a dummy
    private static final int  COLOR_DATA_SIZE = 4;
    final int stride = (POSITION_DATA_SIZE + COLOR_DATA_SIZE) * BYTES_PER_FLOAT;
    final FloatBuffer cubePositionsBuffer;
    ShortBuffer indexBuffer;

    static float[] cubePositions = { // Using a packed buffer (coordinates x, y, z, 1, and then color R G B A)
     // According to Apple, best practice is to use inter-leaved vertex data (coordinate and color)
     // padding coordinate to 4 per stride for word alignment
            -1, -1, -1, 1 ,0, 0, 0, 1,
             1, -1, -1, 1, 0, 1, 1, 1,
             1,  1, -1, 1, 1, 1, 1, 1,
            -1,  1, -1, 1, 0, 0, 1, 1,
            -1, -1,  1, 1, 1, 0, 1, 1,
             1, -1,  1, 1, 1, 0, 0, 1,
             1,  1,  1, 1, 0, 1, 0, 1,
            -1,  1,  1, 1, 1, 1, 0, 1
    };

    static short[] indexArray = {  // starts from 0
            0, 4, 5, 0, 5, 1, 1, 5, 6, 1, 6, 2, 2, 6, 7, 2, 7, 3, 3, 7, 4, 3, 4, 0, 4, 7, 6, 4, 6,
            5, 3, 0, 1, 3, 1, 2
    };
    private final int mProgram;
    @SuppressWarnings("FieldCanBeLocal")
    private int vPMatrixHandle;
    @SuppressWarnings("FieldCanBeLocal")
    private int mPositionHandle;
    @SuppressWarnings("FieldCanBeLocal")
    private int mColorHandle;
/*
attribute - Global variables that may change per vertex, that are passed from the OpenGL
   application to vertex shaders. This qualifier can only be used in vertex shaders.
   For the shader this is a read-only variable
uniform - Global variables that may change per primitive that are passed from the OpenGL
   application to the shaders. This qualifier can be used in both vertex and fragment shaders.
   For the shaders this is a read-only variable
varying - used for interpolated data between a vertex shader and a fragment shader.
   Available for writing in the vertex shader, and read-only in a fragment shader.
*/
    private final static String vertexShaderCode =
"attribute vec4 vPosition;" +
"attribute vec4 aColor;" +
"uniform mat4 uMVPMatrix;" +
"varying vec4 vColor;" +
"void main() {" +
"  gl_Position = uMVPMatrix * vPosition;" +
"  vColor = aColor;" +
"  }";
    private final static String fragmentShaderCode =
"precision mediump float;" +
"varying vec4 vColor;" +
"void main() {" +
"  gl_FragColor = vColor;" +
"}";
    public Cube() {

       // Allocate a direct block of memory on the native heap
       cubePositionsBuffer = ByteBuffer.allocateDirect(cubePositions.length * BYTES_PER_FLOAT)
       // Floats can be in big-endian or little-endian order.
        .order(ByteOrder.nativeOrder())
       // Give us a floating-point view on this byte buffer.
        .asFloatBuffer();

       // Copy data from the Java heap to the native heap.
        cubePositionsBuffer.put(cubePositions)
       // Reset the buffer position to the beginning of the buffer.
        .position(0);
       // Once the data is on the native heap, we no longer need to keep the float[] array
       // around, and we can let the garbage collector clean it up.


        // indexBuffer = Buffers.newDirectIntBuffer(indexArray);
        // maybe ES does not support int index buffer
        indexBuffer = ByteBuffer.allocateDirect(indexArray.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        indexBuffer.put(indexArray).position(0);

        int vertexShader = CubeRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = CubeRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        if (mProgram == 0)
            throw new RuntimeException("Error creating program.");

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);

    } // constructor

    public void draw(float[] mvpMatrix) {

        // enable face culling feature
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        // specify which faces to not draw
        GLES20.glCullFace(GLES20.GL_BACK);
        // set counter-clockwise faces as front-facing
        GLES20.glFrontFace(GLES20.GL_CW);

        // pre-calculate matrix multiplication at CPU once instead of multiple (per vertex) in GPU
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // Pass in the position information
        cubePositionsBuffer.position(0);

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, POSITION_DATA_SIZE,
                GLES20.GL_FLOAT, false, stride, cubePositionsBuffer);

        // Pass in the normal information
        cubePositionsBuffer.position(POSITION_DATA_SIZE);

        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");

        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, COLOR_DATA_SIZE,
                GLES20.GL_FLOAT, false, stride, cubePositionsBuffer);


        // get handle to shape's transformation matrix
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        // GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        GLES20.glDrawElements( GLES20.GL_TRIANGLES, indexArray.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer );
        // GL_QUADS is going to be deprecated

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mColorHandle);

    } // draw

}
