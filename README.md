# OpenGL_sensor
This is my first exercise learning OpenGL 2.0 ES in Android.  I would like to share my experience of integrating with SensorManager to give a "First Person Camera View".

**SensorManager.getRotationMatrix** will return a Rotation matrix which can transform a vector from the device coordinate system to the world's coordinate system.  Although documentation states that this matrix is ready to be used by OpenGL ES, I have overlooked some important points initially

1. This matrix is row-major, unlike the OpenGL matrix which is column major
2. During the openGL rendering, the rotation matrix is *from* the world coordinates *to* the device coordinates.
3. Therefore luckily, the inverse of the matrix is to be used.  But Rotation matrix has characteristics that to transpose it effective give the inverse.  To transpose also means to swap the column major to row major.  Therefore the rotation matrix can be used without manipulation at all when passing to OpenGL
4. But I have still omitted another point, which I will say later on.

To pass the Rotation matrix from SensorManager to OpenGL, it can be as follows:

1. inside **onSensorChanged** method, capture the matrix via SensorManager.getRotationMatrix.  If the result is true, pass it to the SurfaceView class.  (I find that there are cases indeed the result can be false and the Rotation Matrix contains all zeros!)
2. then the SurfaceView class cascades the matrix  to the Render class and first a requestRender()
3. in onDrawFrame method of the Renderer class, this matrix can be used to populate the Model matrix directly (without any transpose logic)
Then using the phone to pan/tilt/roll, then OpenGL can render the image correctly.

But I have not yet said how to handle camera forward movement scenario.

In OpenGL, the viewMatrix is populated by the Matrix.setLookAtM method.  As a convention, I have set

eye = (0, 0, 0) // origin

center = (0, 0, 1) // looking to z axis 

up = (0, 1, 0) // y axis

So, at first thought, changing the origin of the "First Person Camera" can simply update the viewMatrix with a new eye coordinates.  However, there are two disadvantages

convention is to change the model Matrix as a whole (with other logic, say, rotation)
I also use the model matrix to render multiple some objects and so I am happy to keep eye origin unchanged in the view Matrix
To move forward effectively changes the origin by incrementing z.  Since we need to first translate-then-rotate, we need to translate the (inverse of rotation matrix) times (0, 0, 1)

For a rotation matrix of

r<sub>11</sub> r<sub>12</sub> r<sub>13</sub>

r<sub>21</sub> r<sub>22</sub> r<sub>23</sub>

r<sub>31</sub> r<sub>32</sub> r<sub>33</sub>

its inverse (or transpose) is

r<sub>11</sub> r<sub>21</sub> r<sub>31</sub>

r<sub>12</sub> r<sub>22</sub> r<sub>32</sub>

r<sub>13</sub> r<sub>23</sub> r<sub>33</sub>

multiplying the z unit vector becomes

r<sub>31</sub>

r<sub>32</sub>

r<sub>33</sub>

For a row major resprsentation, it is the 2nd, 6th, 10th elements in the 4x4 matrix.

So actually I need to translate the model matrix by (-r[2], -r[6], -r[10])

This is the lesson I have learnt.
