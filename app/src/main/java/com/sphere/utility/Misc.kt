package com.sphere.utility

import android.app.Activity
import android.content.Context
import android.util.Log
import com.sphere.R

private const val TAG = "Misc"

fun setSelectedSpherePref(activity: Activity, sphereName: String) {
    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)

    with (sharedPref.edit()) {
        putString(activity.getString(R.string.selected_sphere), sphereName)
        apply()
    }
    Log.i(TAG, "Set ${activity.getString(R.string.selected_sphere)} to \"$sphereName\"")
}

fun getSelectedSpherePref(activity: Activity): String? {
    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)

    return sharedPref.getString(activity.getString(R.string.selected_sphere), "")
}
