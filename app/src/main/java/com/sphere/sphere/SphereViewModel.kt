package com.sphere.sphere

import android.util.Log
import androidx.lifecycle.*
import com.sphere.sphere.room_code.Sphere
import com.sphere.sphere.room_code.SphereRepository
import kotlinx.coroutines.launch

class SphereViewModel(private val repository: SphereRepository): ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allSpheres: LiveData<List<Sphere>> = repository.allSpheres

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(sphere: Sphere) = viewModelScope.launch {
        repository.insert(sphere)
    }

    // Current Sphere info
    private var currSphere = ""
    private var vertices: MutableList<Float> = mutableListOf()
    private var normals: MutableList<Float> = mutableListOf()
    private var indices: MutableList<Short> = mutableListOf()
    private var lineIndices: MutableList<Short> = mutableListOf()
    private var colors: MutableList<Float> = mutableListOf()

    private fun unloadSphere() {
        currSphere = ""
        vertices = mutableListOf()
        normals = mutableListOf()
        indices = mutableListOf()
        lineIndices = mutableListOf()
        colors = mutableListOf()
    }

    // --- Functions for retrieving and changing ViewModel data ---

    fun newSphere(name: String) {
        currSphere = name
        // TODO : This needs to get changed eventually so it's not just the unloadSphere implementation.
        vertices = mutableListOf()
        normals = mutableListOf()
        indices = mutableListOf()
        lineIndices = mutableListOf()
        colors = mutableListOf()
    }

    fun loadSphere(
        name:String,
        loadedVertices:MutableList<Float>,
        loadedNormals: MutableList<Float>,
        loadedIndices: MutableList<Short>,
        loadedLineIndices: MutableList<Short>,
        loadedColors: MutableList<Float>,
    ) {
        currSphere = name
        vertices = loadedVertices
        normals = loadedNormals
        indices = loadedIndices
        lineIndices = loadedLineIndices
        colors = loadedColors
    }

    // Grabs the nth vertex and returns it as MutableList<Float>
    // Keep in mind to grab the first vert, n = 0 must be used.
    fun getVert(n: Int): MutableList<Float> {
        return mutableListOf(vertices[n * 3], vertices[(n * 3) + 1], vertices[(n * 3) + 2])
    }

    // Takes an array of indices and returns the respective vertices as a FloatArray
    fun getSelectionOfVertices(n: IntArray): MutableList<Float> {
        var retVertices = mutableListOf<Float>()
        for (i in n.indices) {
            retVertices.add(vertices[i*3])
            retVertices.add(vertices[(i*3)+1])
            retVertices.add(vertices[(i*3)+2])
        }
        return retVertices
    }

    // Returns this sphere's vertices
    fun getAllVertices(): MutableList<Float> {
        return vertices
    }

    // Adds a vertice to this sphere
    fun addVerticeToSphere(newVertice: MutableList<Float>) {
        vertices.add(newVertice[0])
        vertices.add(newVertice[1])
        vertices.add(newVertice[2])
    }

    // Updates an existing vertice n with a new vertice
    fun updateVerticeInSphere(n: Int, newVertice: MutableList<Float>) {
        vertices[n] = newVertice[0]
        vertices[n+1] = newVertice[1]
        vertices[n+2] = newVertice[2]
    }

    // Returns this sphere's normals
    fun getAllNormals(): MutableList<Float> {
        return normals
    }

    // Returns this sphere's indices
    fun getAllIndices(): MutableList<Short> {
        return indices
    }

    // Returns this sphere's line indices
    fun getAllLineIndices(): MutableList<Short> {
        return lineIndices
    }

    // Returns this sphere's line indices
    fun getAllColors(): MutableList<Float> {
        return colors
    }

    fun mutateSphere() {
        // TODO : should be passed the params it's going to use to change the vertices.
        // Also a selection of vertices should be passed, if user didn't select any it's just all of them
        // We'll need to make some operations in here that combine the sensor data and seed in such
        //     a way that the sphere is somewhat uniquely changed for each mutation.
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
