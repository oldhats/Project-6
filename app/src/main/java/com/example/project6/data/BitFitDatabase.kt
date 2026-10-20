package com.example.project6.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BitFitEntity::class], version = 1, exportSchema = false)
abstract class BitFitDatabase : RoomDatabase() {
    abstract fun bitFitDao(): BitFitDao

    companion object {
        @Volatile
        private var INSTANCE: BitFitDatabase? = null

        fun getDatabase(context: Context): BitFitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BitFitDatabase::class.java,
                    "bitfit_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
