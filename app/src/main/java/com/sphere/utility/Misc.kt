package com.sphere.utility

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.opengl.GLException
import android.os.Environment
import android.util.Log
import android.view.View
import com.sphere.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.IntBuffer
import javax.microedition.khronos.opengles.GL10

private const val TAG = "Misc"

// =========================================================================
// SELECTED_SPHERE Preference stuff

fun setSelectedSpherePref(activity: Activity, sphereName: String) {
    val sharedPref = activity.getSharedPreferences(null, Context.MODE_PRIVATE)

    with (sharedPref.edit()) {
        putString(activity.getString(R.string.selected_sphere), sphereName)
        apply()
    }
    Log.i(TAG, "Set ${activity.getString(R.string.selected_sphere)} to \"$sphereName\"")
}

fun getSelectedSpherePref(activity: Activity): String? {
    val sharedPref = activity.getSharedPreferences(null, Context.MODE_PRIVATE)

    return sharedPref.getString(activity.getString(R.string.selected_sphere), "")
}

fun getSelectedSpherePref(context: Context): String? {
    val sharedPref = context.getSharedPreferences(null, Context.MODE_PRIVATE)

    return sharedPref.getString(context.getString(R.string.selected_sphere), "")
}

// =========================================================================
// Screenshot stuff

// https://stackoverflow.com/questions/5514149/capture-screen-of-glsurfaceview-to-bitmap
fun createBitmapFromGLSurfaceView(x: Int, y: Int, w: Int, h: Int, gl: GL10): Bitmap? {
    val bitmapBuffer = IntArray(w * h)
    val bitmapSource = IntArray(w * h)
    val intBuffer: IntBuffer = IntBuffer.wrap(bitmapBuffer)
    intBuffer.position(0)

    try {
        gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer)
        var offset1: Int
        var offset2: Int

        for (i in 0 until h) {
            offset1 = i * w
            offset2 = (h - i - 1) * w

            for (j in 0 until w) {
                val texturePixel = bitmapBuffer[offset1 + j]
                val blue = texturePixel shr 16 and 0xff
                val red = texturePixel shl 16 and 0x00ff0000
                val pixel = texturePixel and -0xff0100 or red or blue
                bitmapSource[offset2 + j] = pixel
            }
        }
    } catch (e: GLException) {
        Log.e(TAG, e.message, e)
        return null
    }

    return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888)
}

// https://handyopinion.com/convert-bitmap-to-file-in-android-java-kotlin/
fun saveSphereBitmap(context: Context, sphereName: String, bitmap: Bitmap){
    val fPath = context.getExternalFilesDir(null)?.absolutePath

    val file = File(fPath + File.separator + sphereName)
    file.createNewFile()

    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)

    FileOutputStream(file).apply {
        write(bos.toByteArray())
        flush()
        close()
    }
    Log.i(TAG, "Saved a bitmap to ${fPath + File.separator + sphereName}")
}

// =========================================================================
// Math stuff

fun interpolate(a1: Float, a2: Float, b1: Float, b2: Float, x: Float): Float {
    return b1 + ((x - a1) * (b2 - b1) / (a2 - a1))
}
