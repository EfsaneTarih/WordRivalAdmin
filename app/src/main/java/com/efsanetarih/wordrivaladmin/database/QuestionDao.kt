package com.efsanetarih.wordrivaladmin.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.efsanetarih.wordrivaladmin.model.Question


@Dao
interface QuestionDao {
    @get:Query("SELECT * FROM Questions")
    val getWord: LiveData<List<Question?>?>?

    @Insert
    fun insertAll(word: Question?)

    @Query("DELETE FROM Questions")
    fun deleteAllQuestions()



}