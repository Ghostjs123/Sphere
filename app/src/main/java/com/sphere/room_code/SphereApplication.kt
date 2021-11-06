package com.sphere.room_code

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class SphereApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    private val database by lazy { SphereDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { SphereRepository(database.sphereDao()) }
}
