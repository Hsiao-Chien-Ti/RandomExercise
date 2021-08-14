package com.example.randomexercise

import android.app.Application
import com.example.randomexercise.data.ExRoomDatabase

class RandomExerciseApplication : Application() {
    val database: ExRoomDatabase by lazy { ExRoomDatabase.getDatabase(this) }
}