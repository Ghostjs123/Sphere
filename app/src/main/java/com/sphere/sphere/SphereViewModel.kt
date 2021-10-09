package com.sphere.sphere

import android.util.Log
import androidx.lifecycle.ViewModel

class SphereViewModel: ViewModel() {

    // Name of the current sphere
    private var currSphere = ""
    // Collection of current vertices
    private var vertices: MutableList<Float> = mutableListOf()
    // Collection of current normals
    private var normals: MutableList<Float> = mutableListOf()
    // Collection of current indices
    private var indices: MutableList<Short> = mutableListOf()
    // Collection of current line indices
    private var lineIndices: MutableList<Short> = mutableListOf()
    // Seed value of this sphere
    private var seed = 0

    private fun unloadSphere() {
        currSphere = ""
        vertices = mutableListOf()
        normals = mutableListOf()
        indices = mutableListOf()
        lineIndices = mutableListOf()
        seed = 0
    }

    // --- Functions for retrieving and changing ViewModel data ---

    fun newSphere(name: String) {
        currSphere = name
        // TODO : This needs to get changed eventually so it's not
        //        just the unloadSphere implementation.
        vertices = mutableListOf()
        normals = mutableListOf()
        indices = mutableListOf()
        lineIndices = mutableListOf()
        seed = (0..99999).random()
    }

    fun loadSphere(name:String, loadedVertices: MutableList<Float>, loadedNormals: MutableList<Float>,
                   loadedIndices: MutableList<Short>, loadedLineIndices: MutableList<Short>, importSeed: Int) {
        currSphere = name
        vertices = loadedVertices
        normals = loadedNormals
        indices = loadedIndices
        lineIndices = loadedLineIndices
        seed = importSeed
    }

    // Grabs the nth vertice and returns it as MutableList<Float>
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