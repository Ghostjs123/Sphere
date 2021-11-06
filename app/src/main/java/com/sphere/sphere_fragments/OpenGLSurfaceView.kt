package com.sphere.sphere_fragments

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import com.sphere.Icosphere
import kotlin.math.abs

private const val TAG = "OpenGLSurfaceView"

private const val TOUCH_SCALE_FACTOR: Float = 0.2f

class OpenGLSurfaceView(context: Context, attrs: AttributeSet) : GLSurfaceView(context, attrs) {

    private val renderer: OpenGLRenderer

    private var currX: Float = 0f
    private var currY: Float = 0f
    private var prevX: Float = 0f
    private var prevY: Float = 0f
    private var hasDragged: Boolean = false

    init {
        Log.i(TAG, "init Started")

        setEGLContextClientVersion(1)

        renderer = OpenGLRenderer()
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY

        Log.i(TAG, "init Ended")
    }

    fun createNewSphere(seed: Long?, subdivision: Int) {
        renderer.icosphere = Icosphere(subdivision)
        renderer.icosphere!!.buildVertices()
        renderer.icosphere!!.mutate(seed)
        requestRender()
    }

    fun mutateSphere(seed: Long?) {
        Log.i(TAG, "Mutating Sphere using seed: $seed")

        renderer.icosphere?.mutate(seed)
        requestRender()
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x: Float = e.x
        val y: Float = e.y

        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                hasDragged = false
            }
            MotionEvent.ACTION_MOVE -> {
                renderer.cameraAngleX += (y - prevY) * TOUCH_SCALE_FACTOR

                val directionHandler = abs((renderer.cameraAngleX.toInt() + 90) / 180) % 2

                if (directionHandler != 0)
                    renderer.cameraAngleY -= (x - prevX) * TOUCH_SCALE_FACTOR
                else
                    renderer.cameraAngleY += (x - prevX) * TOUCH_SCALE_FACTOR

                hasDragged = true
                requestRender()
            }
            MotionEvent.ACTION_UP -> {
                prevX = x
                prevY = y
                if (!hasDragged) performClick()
            }
        }

        prevX = x
        prevY = y

        return true
    }

    override fun performClick(): Boolean {
        super.performClick()

        renderer.performClick(prevX, prevY)

        return true
    }
}