package com.sphere.sphere

import android.util.Log
import androidx.lifecycle.*
import com.sphere.sphere.room_code.Sphere
import com.sphere.sphere.room_code.SphereRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SphereViewModel(private val repository: SphereRepository): ViewModel() {

    val allSpheres: LiveData<List<Sphere>> = repository.allSpheres.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(sphere: Sphere) = viewModelScope.launch {
        repository.insert(sphere)
    }

    fun update(name: String, seed: Long) = viewModelScope.launch {
        repository.update(name, seed)
    }

    // Current Sphere info
    private var sphereName = ""
    private var seed: Long? = 0
    private var index: Int = 0

    private fun unloadSphere() {
        sphereName = ""
        seed = 0
        index = 0
    }

    // --- Functions for retrieving and changing ViewModel data ---

    fun newSphere(name: String) {
        sphereName = name
        seed = 0
        index = 0
    }

    // Loads the sphere at sphereIndex from the LiveData
    fun loadSphere(sphereIndex:Int) {
        val currSphereList = allSpheres.value
        sphereName = currSphereList!![sphereIndex].name
        seed = currSphereList!![sphereIndex].seed
        index = sphereIndex
    }

    // Returns this sphere's name
    fun getName(): String {
        return sphereName
    }

    // Sets this sphere's name
    fun setName(newName:String) {
        // TODO : This needs to also update the sphere's name in our LiveData
        sphereName = newName
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

    init {
        Log.i("SphereViewModel", "SphereViewModel created")
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
