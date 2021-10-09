package com.sphere.sphere

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import kotlin.math.abs


private const val TOUCH_SCALE_FACTOR: Float = 0.2f
private const val TAG = "OpenGLSurfaceView"

class OpenGLSurfaceView(context: Context) : GLSurfaceView(context) {

    private val renderer: OpenGLRenderer

    private var prevX: Float = 0f
    private var prevY: Float = 0f

    init {
        setEGLContextClientVersion(1)

        renderer = OpenGLRenderer()
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x: Float = e.x
        val y: Float = e.y

        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                Log.i(TAG, renderer.cameraAngleX.toString())
                renderer.cameraAngleX += (y - prevY) * TOUCH_SCALE_FACTOR

                var directionHandler = abs((renderer.cameraAngleX.toInt()+90) / 180) % 2
                Log.i(TAG, directionHandler.toString())
                if (directionHandler != 0)
                    renderer.cameraAngleY -= (x - prevX) * TOUCH_SCALE_FACTOR
                else
                renderer.cameraAngleY += (x - prevX) * TOUCH_SCALE_FACTOR
                requestRender()
            }
        }

        prevX = x
        prevY = y

        return true
    }
}