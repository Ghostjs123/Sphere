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
    fun delete(sphereName: String) = viewModelScope.launch {
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
        // TODO : This needs to also update the sphere's name in our LiveData
        sphereName = newName
        insert(Sphere(sphereName, seed, subdivisions))
        //updateName(sphereName, newName)
    }

    // Returns this sphere's seed
    fun getSeed(): Long? {
        return seed
    }

    // Sets this sphere's seed
    fun setSeed(newSeed: Long?) {
        seed = newSeed
        // TODO - ViewModel private vars are being cleared before we reach here, fix that.
        updateSeed(sphereName, newSeed)
    }

    // Returns this sphere's subdivisions
    fun getSubdivisions(): Int {
        return subdivisions
    }

    fun isEmpty(): Boolean {
        return if (allSpheres.value == null) {
            true
        } else {
            allSpheres.value!!.isEmpty()
        }
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
