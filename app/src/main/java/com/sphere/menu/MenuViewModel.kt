package com.sphere.menu

import android.util.Log
import androidx.lifecycle.ViewModel

class MenuViewModel: ViewModel() {

    // Name of all spheres (Paths may be derived from these)
    var sphereList: MutableList<String> = mutableListOf()

    private fun unloadSphere() {
        sphereList = mutableListOf()
    }

    // Functions for changing ViewModel data

    // Returns true if a sphere of name 'name' was added, false otherwise
    fun addSphere(name: String): Boolean {
        if (sphereList.contains(name))
            return false
        else
            sphereList.add(name)
        return true
    }

    // Returns true if a sphere of name 'name' was removed or not present, false otherwise
    fun removeSphere(name: String): Boolean {
        if (!sphereList.contains(name))
            return true
        else
            sphereList.remove(name)
        return true
    }

    init {
        Log.i("SphereViewModel", "SphereViewModel created")
    }
}