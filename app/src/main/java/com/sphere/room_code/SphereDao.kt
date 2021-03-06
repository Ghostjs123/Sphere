package com.sphere.room_code

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SphereDao {

    @Query("SELECT * FROM sphere_table ORDER BY name ASC")
    fun getAlphabetizedSpheres(): Flow<List<Sphere>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sphere: Sphere)

    @Query("UPDATE sphere_table SET seed=:seed WHERE name=:name")
    suspend fun updateSeed(name: String?, seed: Long?)

    @Query("UPDATE sphere_table SET name=:newName WHERE name=:oldName")
    suspend fun updateSeed(oldName: String, newName: String)

    @Query("DELETE FROM sphere_table WHERE name=:name")
    suspend fun delete(name: String?)

    @Query("DELETE FROM sphere_table")
    suspend fun deleteAll()
}
