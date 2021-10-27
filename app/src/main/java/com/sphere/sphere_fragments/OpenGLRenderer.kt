package com.sphere.sphere_fragments

import android.opengl.*
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.util.Log
import com.sphere.Icosphere

private const val TAG = "OpenGLRenderer"


var cameraDistance = 3.0f


class OpenGLRenderer : GLSurfaceView.Renderer {

    @Volatile
    var cameraAngleX = 0f

    @Volatile
    var cameraAngleY = 0f

    @Volatile
    var icosphere: Icosphere? = null

    private lateinit var mGL: GL10

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        Log.i(TAG, "onSurfaceCreated() Started")
        gl.glShadeModel(GL10.GL_SMOOTH)
        gl.glPixelStorei(GL10.GL_UNPACK_ALIGNMENT, 4)

        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST)
        gl.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_NICEST)

        gl.glEnable(GL10.GL_CULL_FACE)
        gl.glEnable(GL10.GL_LIGHTING)
        gl.glEnable(GL10.GL_TEXTURE_2D)
        gl.glEnable(GL10.GL_DEPTH_TEST)
        gl.glEnable(GL10.GL_BLEND)
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)

        gl.glEnable(GL10.GL_COLOR_MATERIAL)

        gl.glClearColor(0.15f, 0.15f, 0.15f, 1.0f)
        gl.glClearDepthf(1f)
        gl.glClearStencil(0)
        gl.glDepthFunc(GL10.GL_LEQUAL)

        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, floatArrayOf(0.3f, 0.3f, 0.3f, 1.0f), 0)
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, floatArrayOf(0.7f, 0.7f, 0.7f, 1.0f), 0)
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f), 0)
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, floatArrayOf(1.0f, 1.0f, 1.0f, 0.0f), 0)
        gl.glEnable(GL10.GL_LIGHT0)

        mGL = gl

        Log.i(TAG, "onSurfaceCreated() Finished")
    }

    override fun onDrawFrame(gl: GL10) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT or GL10.GL_STENCIL_BUFFER_BIT)

        gl.glLoadIdentity()

        gl.glTranslatef(0f, 0f, -cameraDistance)
        gl.glRotatef(cameraAngleX, 1f, 0f, 0f)   // pitch
        gl.glRotatef(cameraAngleY, 0f, 1f, 0f)   // heading

        gl.glRotatef(-90f, 1f, 0f, 0f)

        val ambient = FloatBuffer.wrap(floatArrayOf(0.2f, 0.2f, 0.2f, 1f ))
        val diffuse = FloatBuffer.wrap(floatArrayOf(0.8f, 0.8f, 0.8f, 1f))
        val specular = FloatBuffer.wrap(floatArrayOf(1.0f, 1.0f, 1.0f, 1f))
        val shininess = 128f
        gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_AMBIENT, ambient)
        gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_DIFFUSE, diffuse)
        gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_SPECULAR, specular)
        gl.glMaterialf(GL10.GL_FRONT, GL10.GL_SHININESS, shininess)

        icosphere?.draw(gl)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        gl.glViewport(0, 0, width, height)

        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()

        GLU.gluPerspective(gl, 45.0f, width.toFloat() / height.toFloat(),0.1f, 100.0f)

        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
    }

    fun performClick(x: Float, y: Float) {

    }
}