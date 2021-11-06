package com.sphere.room_code

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Sphere::class], version = 1, exportSchema = false)
abstract class SphereDatabase : RoomDatabase() {

    abstract fun sphereDao(): SphereDao

    companion object {
        @Volatile
        private var INSTANCE: SphereDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SphereDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SphereDatabase::class.java,
                    "sphere_database"
                ).build()

                // return instance
                INSTANCE = instance
                instance
            }
        }
    }
}
