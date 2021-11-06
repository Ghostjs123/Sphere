package com.sphere.room_code

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class SphereRepository(private val sphereDao: SphereDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allSpheres: Flow<List<Sphere>> = sphereDao.getAlphabetizedSpheres()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(sphere: Sphere) {
        sphereDao.insert(sphere)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateSeed(name: String, seed: Long?) {
        sphereDao.updateSeed(name, seed)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(name: String) {
        sphereDao.delete(name)
    }
}
