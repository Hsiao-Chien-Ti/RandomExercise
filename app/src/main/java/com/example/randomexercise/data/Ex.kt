package com.example.randomexercise.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Ex(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,//just use to avoid data conflict
    @ColumnInfo(name = "subject")
    val subject: String,
    @ColumnInfo(name = "chapter")
    val chapter: Int,
    @ColumnInfo(name = "number")
    val number: Int,
    @ColumnInfo(name = "correct")
    val correct: Int//-1->notyet/0->wrong/1->correct
)

