package com.sphere.sphere

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sphere_table")
data class Sphere(@PrimaryKey @ColumnInfo(name = "name")val name: String,
                  @ColumnInfo(name = "vertices")val vertices: MutableList<Float>,
                  @ColumnInfo(name = "normals")val normals: MutableList<Float>,
                  @ColumnInfo(name = "indices")val indices: MutableList<Short>,
                  @ColumnInfo(name = "line_indices")val lineIndices: MutableList<Short>,
                  @ColumnInfo(name = "colors")val colors: MutableList<Float>,
                  val seed: Int) {

}