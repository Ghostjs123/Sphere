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
    fun update(name: String, seed: Long) = viewModelScope.launch {
        repository.update(name, seed)
    }
    fun delete(sphereName: String) = viewModelScope.launch {
        repository.delete(sphereName)
    }

    // Current Sphere info
    private var sphereName = ""
    private var seed: Long? = 0
    private var subdivisions: Int = 0
    private var index: Int = 0

    // ================================================================================
    // --- Functions for retrieving and changing ViewModel data ---

    fun newSphere(name: String) {
        sphereName = name
        seed = 0
        subdivisions = 0
        index = 0
    }

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
        //update(sphereName, seed)
    }

    // Returns this sphere's seed
    fun getSeed(): Long? {
        return seed
    }

    // Sets this sphere's seed
    fun setSeed(newSeed: Long?) {
        // TODO : This needs to also update the sphere's seed in our LiveData
        seed = newSeed
        //update(sphereName, seed)
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
