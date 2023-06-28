package com.example.opengl_sensor;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/*
Changed to color interpolation
https://stackoverflow.com/questions/34634348/non-uniform-color-values-for-fragment-shader-in-opengl-es-2-0
https://stackoverflow.com/questions/21942010/fragment-shader-color-interpolation-details-and-hardware-support
 */
public class Triangle {

    static float[] triangleCoords = {   // in counterclockwise order:
            0.0f,  0.622008459f, -0.9f, // top
            -0.5f, -0.311004243f, 0.0f, // bottom left
            0.5f, -0.311004243f, 0.0f  // bottom right
    };

    // Set color with red, green, blue and alpha (opacity) values
    // float[] color = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
    // changed to different for each vertex
    static float[] triangleColors = {
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f
    };
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static final int COLORS_PER_VERTEX = 4;
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private final int colorStride = COLORS_PER_VERTEX * 4;
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    private int positionHandle;
    // private int colorHandle;
    private int mColorHandle;
    // Use to access and set the view transformation
    private int vPMatrixHandle;
    private final int mProgram;

    /*  This is the minimal vertex shader in which gl_Vertex of type vec4 with the predefined
        uniform gl_ModelViewProjectionMatrix of type mat4 and stores the result in the predefined output
        variable gl_Position of type vec4.

     */
    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            // An attribute and an uniform are quite similar but
            // attribute is per vertex while the uniform is per draw call
            // (will have the same value for all vertices, fragments).
            "attribute vec4 vPosition;" +
            "attribute vec4 aColor;" +
            "uniform mat4 uMVPMatrix;" +
            // varying is a bit different. Usually a varying is assigned from
            // the attribute and is done in the vertex shader.
            // This means every vertex will have its own value from the attribute but
            // after rasterization is done each of the varying value will be interpolated
            // depending on the fragment position relative to the bounding vertices.
            // So a varying is designed to communicate between the vertex and the
            // fragment shader (sending data from vertex to fragment shader).
            "varying vec4 vColor;" +
            "void main() {" +
            // the matrix must be included as a modifier of gl_Position
            // Note that the uMVPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            "  gl_Position = uMVPMatrix * vPosition;" +
            "  vColor = aColor;" +
            "  }";

    private final String fragmentShaderCode =
            "precision mediump float;" +
            // "uniform vec4 vColor;" +
            "varying vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
               "}";

    public Triangle() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        ByteBuffer cb = ByteBuffer.allocateDirect(triangleColors.length * 4);
        cb.order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(triangleColors);
        colorBuffer.position(0);


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


    public void draw(float[] mvpMatrix) { // pass in the calculated transformation matrix
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);
        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        // colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        // Set color for drawing the triangle
        // GLES20.glUniform4fv(colorHandle, 1, color, 0);

        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, COLORS_PER_VERTEX, GLES20.GL_FLOAT, false, colorStride, colorBuffer);

        // get handle to shape's transformation matrix
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        // will be changed to glDrawElements
        // glDrawArrays submits the vertices in linear order
        // as they are stored in the vertex arrays.
        // With glDrawElements you have to supply an index buffer.
        // Indices allow you to submit the vertices in any order,
        // and to reuse vertices that are shared between triangles.

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(mColorHandle);
    } // draw()
}
