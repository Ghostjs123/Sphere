package com.sphere.sphere.room_code

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ProvidedTypeConverter
class ListConverter {

    @TypeConverter
    fun FloatListToString(li: MutableList<Float>): String {
        return li.joinToString(separator = ";")
    }

    @TypeConverter
    fun IntListToString(li: MutableList<Int>): String {
        return li.joinToString(separator = ";")
    }

    @TypeConverter
    fun StringToFloatList(str: String): MutableList<Float> {
        return str.split(";").map { it.toFloat() }.toMutableList()
    }

    @TypeConverter
    fun StringToIntList(str: String): MutableList<Int> {
        return str.split(";").map { it.toInt() }.toMutableList()
    }
}

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
                ).addTypeConverter(ListConverter())
                 .addCallback(SphereDatabaseCallback(scope))
                 .build()

                // return instance
                INSTANCE = instance
                instance
            }
        }
    }

    private class SphereDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.sphereDao())
                }
            }
        }

        suspend fun populateDatabase(sphereDao: SphereDao) {
            sphereDao.deleteAll()

            sphereDao.insert(Sphere("test1"))
            sphereDao.insert(Sphere("test2"))
        }
    }
}


