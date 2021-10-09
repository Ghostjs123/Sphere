package com.sphere.sphere

import android.util.Log
import androidx.lifecycle.ViewModel

class SphereViewModel: ViewModel() {

    // Name of the current sphere
    var currSphere = ""
    // List of current vertices
    var vertList: FloatArray = floatArrayOf()
    // Seed value of this sphere
    var seed = 0

    private fun unloadSphere() {
        currSphere = ""
        vertList = floatArrayOf()
        seed = 0
    }

    // Functions for changing ViewModel data

    fun newSphere(name: String) {
        currSphere = name
        vertList = floatArrayOf()
        seed = (0..99999).random()
    }

    fun loadSphere(name:String, vertices: FloatArray, importSeed: Int) {
        currSphere = name
        vertList = vertices
        seed = importSeed
    }


    fun mutateSphere() {
        // TODO : should be passed the params it's going to use to change the verts.
        // Also a selection of verts should be passed, if user didn't select any it's just all of them
        // We'll need to make some operations in here that combine the sensor data and seed in such
        //     a way that the sphere is somewhat uniquely changed for each mutation.
    }

    init {
        Log.i("SphereViewModel", "SphereViewModel created")
    }
}