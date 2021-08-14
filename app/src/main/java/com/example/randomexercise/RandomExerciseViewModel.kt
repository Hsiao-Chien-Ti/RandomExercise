package com.example.randomexercise

import android.util.Log
import androidx.lifecycle.*
import com.example.randomexercise.data.Ex
import com.example.randomexercise.data.ExDao
import com.example.randomexercise.data.Subject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RandomExerciseViewModel(private val ExDao: ExDao) : ViewModel() {
    val allSubject: LiveData<List<Subject>> = ExDao.getSubjectList().asLiveData()
    private fun getNewExEntry(subject: String, chapter: String, number: Int): Ex {
        return Ex(
            subject = subject,
            chapter = chapter.toInt(),
            number = number,
            correct = -1
        )
    }

    val subjectNames: LiveData<List<String>> = ExDao.getSubjectName().asLiveData()
    val subjectWrongNames: LiveData<List<String>> = ExDao.getWrongSubjectName().asLiveData()
    private fun getNewSubjectEntry(subject: String, remain: Int, wrong: Int): Subject {
        return Subject(
            subject = subject,
            total = remain + wrong,
            remain = remain,
            wrong = wrong
        )
    }

    private fun getUpdateSubjectEntry(
        subject: String,
        total: Int,
        remain: Int,
        wrong: Int
    ): Subject {
        return Subject(
            subject = subject,
            total = total,
            remain = remain,
            wrong = wrong
        )
    }

    private fun insertEx(Ex: Ex) {
        viewModelScope.launch {
            Log.d("debug", Ex.subject)
            ExDao.insert(Ex)
        }
    }

    private fun insertSubject(subject: Subject) {
        viewModelScope.launch {
            ExDao.insertSubject(subject)
        }
    }

    fun addEx(subject: String, chapter: String, total: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val exist = ExDao.exist(subject)
            if (exist == null) {
                val newSubject = getNewSubjectEntry(subject, total.toInt(), 0)
                insertSubject(newSubject)
            } else {
                val newSubject =
                    getNewSubjectEntry(subject, exist.remain + total.toInt(), exist.wrong)
                ExDao.updateSubject(newSubject)
            }
            for (i in 1..total.toInt()) {
                val newEx = getNewExEntry(subject, chapter, i)
                insertEx(newEx)
            }
        }
    }

    fun isEntryValid(subject: String, chapter: String, total: String): Boolean {
        if (subject.isBlank() || chapter.isBlank() || total.isBlank()) {
            return false
        }
        return true
    }

    var pickedChapter = MutableLiveData<Int>()
    var pickedNumber = MutableLiveData<Int>()
    var noFlag = MutableLiveData<Boolean>()
    fun drawLot(subject: String, mode: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            val chapter = ExDao.getChapter(subject)
            var pickedC = chapter[(1..chapter.size).random() - 1]
            lateinit var number: List<Int>
            if (mode == 0 && ExDao.getRemainCount(subject) <= 0) {
                noFlag.postValue(true)
            } else if (mode == 1 && ExDao.getWrongCount(subject) <= 0) {
                noFlag.postValue(true)
            } else {
                noFlag.postValue(false)
            }
            if (mode == 0) {
                number = ExDao.getNewEx(subject, pickedC)
            } else {
                number = ExDao.getWrongEx(subject, pickedC)
            }
            while (number.isEmpty()) {
                pickedC = chapter[(1..chapter.size).random() - 1]
                if (mode == 0) {
                    number = ExDao.getNewEx(subject, pickedC)
                } else {
                    number = ExDao.getWrongEx(subject, pickedC)
                }
            }
            val pickedN = number[(1..number.size).random() - 1]
            pickedChapter.postValue(pickedC)
            pickedNumber.postValue(pickedN)
        }
    }

    fun setCorrect(subject: String, chapter: Int, number: Int, mode: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            ExDao.correctEx(subject, chapter, number)
            val sub = ExDao.exist(subject)
            var remain = sub.remain
            var wrong = sub.wrong
            when (mode) {
                0 -> {
                    remain -= 1
                }
                1 -> {
                    wrong -= 1
                }
            }
            val newEntry = getUpdateSubjectEntry(subject, sub.total, remain, wrong)
            ExDao.updateSubject(newEntry)
        }
    }

    fun setWrong(subject: String, chapter: Int, number: Int, mode: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            ExDao.wrongEx(subject, chapter, number)
            val sub = ExDao.exist(subject)
            var remain = sub.remain
            var wrong = sub.wrong
            when (mode) {
                0 -> {
                    remain -= 1
                    wrong += 1
                }
                1 -> {
                }
            }
            val newEntry = getUpdateSubjectEntry(subject, sub.total, remain, wrong)
            ExDao.updateSubject(newEntry)
        }
    }

    fun setToZero(subject: String, mode: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            when (mode) {
                0 -> ExDao.setRemainZero(subject)
                1 -> ExDao.setWrongZero(subject)
            }
        }
    }

    fun deleteSubject(subject: String) {
        viewModelScope.launch(Dispatchers.Default) {
            ExDao.deleteEx(subject)
            ExDao.deleteSubject(subject)
        }
    }

    fun restart(subject: String, total: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            ExDao.restartEx(subject)
            val newEntry = getNewSubjectEntry(subject, total, 0)
            ExDao.updateSubject(newEntry)
        }
    }

    fun getSubject(subject: String): LiveData<Subject> {
        return ExDao.getSubject(subject).asLiveData()
    }
}

class RandomExerciseViewModelFactory(private val ExDao: ExDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RandomExerciseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RandomExerciseViewModel(ExDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}