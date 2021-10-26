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

//    val locationRequest: LocationRequest = LocationRequest.create().apply {
//        interval = 30
//        fastestInterval = 10
//        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
//        maxWaitTime = 60
//    }
//    val locationCallback: LocationCallback = object : LocationCallback() {
//        override fun onLocationResult(locationResult: LocationResult) {
//            fusedLocationClient?.removeLocationUpdates(this)
//
//            val locationList = locationResult.locations
//            if (locationList.isNotEmpty()) {
//                val location = locationList.last()
//
//                latitude = location.latitude
//                longitude = location.longitude
//                updateUI()
//            }
//        }
//    }
//    fusedLocationClient?.requestLocationUpdates(
//        locationRequest,
//        locationCallback,
//        Looper.getMainLooper()
//    )