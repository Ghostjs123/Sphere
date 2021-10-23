package com.sphere.room_code

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sphere_table")
data class Sphere(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "seed")
    var seed: Long?
)