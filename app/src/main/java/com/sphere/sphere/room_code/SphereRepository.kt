package com.sphere.sphere.room_code

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.sphere.sphere.room_code.Sphere
import com.sphere.sphere.room_code.SphereDao
import kotlinx.coroutines.flow.Flow

class SphereRepository(private val sphereDao: SphereDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allSpheres: LiveData<List<Sphere>> = sphereDao.getAlphabetizedSpheres()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(sphere: Sphere) {
        sphereDao.insert(sphere)
    }
}
