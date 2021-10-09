package com.sphere.sphere

import android.app.Application

class SpheresApplication : Application() {
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { SphereRoomDatabase.getDatabase(this) }
    val repository by lazy { SphereRepository(database.sphereDao()) }
}