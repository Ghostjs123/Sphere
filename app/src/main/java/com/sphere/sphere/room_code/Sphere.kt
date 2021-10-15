package com.sphere.sphere.room_code

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

//    @ColumnInfo(name = "vertices")
//    val vertices: MutableList<Float>,
//
//    @ColumnInfo(name = "normals")
//    val normals: MutableList<Float>,
//
//    @ColumnInfo(name = "indices")
//    val indices: MutableList<Int>,
//
//    @ColumnInfo(name = "line_indices")
//    val lineIndices: MutableList<Int>,
//
//    @ColumnInfo(name = "colors")
//    val colors: MutableList<Float>
) {

}