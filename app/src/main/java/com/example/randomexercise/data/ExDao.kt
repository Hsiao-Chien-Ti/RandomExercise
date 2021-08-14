package com.example.randomexercise.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExDao {

    @Query("SELECT * FROM Subject ORDER BY subject ASC")
    fun getSubjectList(): Flow<List<Subject>>

    @Query("SELECT Distinct subject FROM Subject WHERE remain>0 ORDER BY subject ASC")
    fun getSubjectName(): Flow<List<String>>

    @Query("SELECT Distinct subject FROM Subject WHERE wrong>0 ORDER BY subject ASC")
    fun getWrongSubjectName(): Flow<List<String>>

    @Query("SELECT Distinct chapter FROM Ex WHERE subject=:subject")
    fun getChapter(subject: String): List<Int>

    @Query("SELECT Distinct number FROM Ex WHERE subject=:subject and chapter=:chapter and correct=-1")
    fun getNewEx(subject: String, chapter: Int): List<Int>

    @Query("SELECT Distinct number FROM Ex WHERE subject=:subject and chapter=:chapter and correct=0")
    fun getWrongEx(subject: String, chapter: Int): List<Int>

    @Query("SELECT * FROM Subject WHERE subject=:subject")
    fun exist(subject: String): Subject

    @Query("SELECT * FROM Subject WHERE subject=:subject")
    fun getSubject(subject: String): Flow<Subject>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Ex)

    @Query("DELETE FROM Ex WHERE subject=:subject")
    fun deleteEx(subject: String)

    @Query("DELETE FROM Subject WHERE subject=:subject")
    fun deleteSubject(subject: String)

    @Query("UPDATE Ex SET correct=0 WHERE subject=:subject and chapter=:chapter and number=:number ")
    fun wrongEx(subject: String, chapter: Int, number: Int)

    @Query("UPDATE Ex SET correct=1 WHERE subject=:subject and chapter=:chapter and number=:number  ")
    fun correctEx(subject: String, chapter: Int, number: Int)

    @Query("SELECT wrong FROM Subject WHERE subject=:subject")
    fun getWrongCount(subject: String): Int

    @Query("SELECT remain FROM Subject WHERE subject=:subject")
    fun getRemainCount(subject: String): Int

    @Query("UPDATE Ex SET correct=-1 WHERE subject=:subject")
    fun restartEx(subject: String)

    @Update
    suspend fun updateSubject(subject: Subject)

    @Query("SELECT correct FROM Ex WHERE subject=:subject and chapter=:chapter and number=:number")
    fun getCorrect(subject: String, chapter: Int, number: Int): Int

    @Query("UPDATE Subject SET remain=0 WHERE subject=:subject")
    fun setRemainZero(subject: String)

    @Query("UPDATE Subject SET wrong=0 WHERE subject=:subject")
    fun setWrongZero(subject: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSubject(subject: Subject)
}