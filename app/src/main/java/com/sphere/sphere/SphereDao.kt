package com.sphere.sphere

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SphereDao {

    @Query("SELECT * FROM sphere_table ORDER BY name ASC")
    fun getAlphabetizedSpheres(): Flow<List<Sphere>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sphere: Sphere)

    @Query("DELETE FROM sphere_table")
    suspend fun deleteAll()
}
