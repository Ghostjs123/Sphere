package com.sphere.sphere

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Sphere::class), version = 1, exportSchema = false)
public abstract class SphereRoomDatabase : RoomDatabase() {

    abstract fun sphereDao(): SphereDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: SphereRoomDatabase? = null

        fun getDatabase(context: Context): SphereRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SphereRoomDatabase::class.java,
                    "sphere_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
