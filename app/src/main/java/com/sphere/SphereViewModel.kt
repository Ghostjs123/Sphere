package com.sphere

import android.util.Log
import androidx.lifecycle.*
import com.sphere.room_code.Sphere
import com.sphere.room_code.SphereRepository
import kotlinx.coroutines.launch

private const val TAG = "SphereViewModel"


class SphereViewModel(private val repository: SphereRepository): ViewModel() {
    val allSpheres: LiveData<List<Sphere>> = repository.allSpheres.asLiveData()

    private fun insert(sphere: Sphere) = viewModelScope.launch {
        repository.insert(sphere)
    }
    private fun updateSeed(name: String, seed: Long?) = viewModelScope.launch {
        repository.updateSeed(name, seed)
    }
    private fun updateName(oldName: String, newName: String) = viewModelScope.launch {
        repository.updateName(oldName, newName)
    }
    fun delete(sphereName: String?) = viewModelScope.launch {
        repository.delete(sphereName)
    }

    // Current Sphere info
    private var sphereName = ""
    private var seed: Long? = 0
    private var subdivisions: Int = 0

    // ================================================================================
    // --- Functions for retrieving and changing ViewModel data ---

    fun loadSphere(sphereName: String): Boolean {
        allSpheres.value?.forEach {
            if (it.name == sphereName) {
                this.sphereName = it.name
                this.seed = it.seed
                this.subdivisions = it.subs

                return true
            }
        }
        Log.w(TAG, "ViewModel failed to load sphere with name: $sphereName")
        return false
    }

    fun addSphere(sphereName: String, seed: Long?, subdivisions: Int) {
        insert(Sphere(sphereName, seed, subdivisions))
        this.sphereName = sphereName
        this.seed = seed
        this.subdivisions = subdivisions
    }

    // Returns this sphere's name
    fun getName(): String {
        return sphereName
    }

    // Sets this sphere's name
    fun setName(newName: String) {
        updateName(sphereName, newName)
        sphereName = newName
    }

    // Returns this sphere's seed
    fun getSeed(): Long? {
        return seed
    }

    // Sets this sphere's seed
    fun setSeed(newSeed: Long?) {
        seed = newSeed
        updateSeed(sphereName, newSeed)
    }

    // Returns this sphere's subdivisions
    fun getSubdivisions(): Int {
        return subdivisions
    }

    // Returns the total number of locally stored spheres
    fun getSphereCount(): Int {
        return allSpheres.value!!.size
    }

    // Loads the neighbor of the current sphere
    // Requires that we have > 1 spheres
    fun loadNeighbor() {
        val currSphereIndex = getSphereIndex()
        // If this is not the first sphere, load the previous sphere
        if (currSphereIndex != 0) {
            loadSphere(allSpheres.value!![currSphereIndex-1].name)
        } else { // Else load the next sphere
            loadSphere(allSpheres.value!![currSphereIndex+1].name)
        }
    }

    // Returns the index of the current sphere in our LiveData
    // Returns -1 if the sphere was not found
    private fun getSphereIndex(): Int {
        var index = 0
        allSpheres.value?.forEach {
            if (it.name == sphereName) {
                return index
            } else {
                index++
            }
        }
        return -1
    }
}

class SphereViewModelFactory(private val repository: SphereRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SphereViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SphereViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
