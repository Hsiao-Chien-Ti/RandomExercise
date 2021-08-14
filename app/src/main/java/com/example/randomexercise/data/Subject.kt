package com.example.randomexercise.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Subject(
    @PrimaryKey(autoGenerate = false)
    val subject: String,
    @ColumnInfo(name = "total")
    val total: Int,
    @ColumnInfo(name = "remain")
    val remain: Int,
    @ColumnInfo(name = "wrong")
    val wrong: Int
)