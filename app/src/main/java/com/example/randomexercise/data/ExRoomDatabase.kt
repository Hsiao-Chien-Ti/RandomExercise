package com.example.randomexercise.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Ex::class, Subject::class], version = 3, exportSchema = false)
abstract class ExRoomDatabase : RoomDatabase() {

    abstract fun ExDao(): ExDao

    companion object {
        @Volatile
        private var INSTANCE: ExRoomDatabase? = null

        fun getDatabase(context: Context): ExRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExRoomDatabase::class.java,
                    "Ex_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
