package com.sphere.utility

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

private const val TAG = "LocationPermissions"

private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
private const val MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66


fun hasCoarseLocationAccess(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun requestLocationPermission(activity: Activity) {
    ActivityCompat.requestPermissions(
        activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
        0
    )
}

fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
) {
    when (requestCode) {
        MY_PERMISSIONS_REQUEST_LOCATION -> {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                mutateSphere()
            }
            else {
                Log.i(TAG, "Location permissions denied")
                // TODO: handle permission denied case
            }
        }
        else -> {
            Log.i(TAG, "Location permissions denied")
            // TODO: handle permission denied case
        }
    }
}