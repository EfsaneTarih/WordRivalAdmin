package com.efsanetarih.wordrivaladmin.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Questions")
class Question {
    @PrimaryKey(autoGenerate = true)
    var questionId: Int = 0

    @ColumnInfo(name = "word")
    var word: String = ""

    @ColumnInfo(name = "answerTrue")
    var answerTrue: String = ""

    @ColumnInfo(name = "answerFalse1")
    var answerFalse1: String = ""

    @ColumnInfo(name = "answerFalse2")
    var answerFalse2: String = ""

    @ColumnInfo(name = "answerFalse3")
    var answerFalse3: String = ""

    @ColumnInfo(name = "type")
    var type: Int = 1

    @ColumnInfo(name = "level")
    var level: Int = 1

    @ColumnInfo(name = "user1Answer")
    var user1Answer: Int = 0

    @ColumnInfo(name = "user2Answer")
    var user2Answer: Int = 0

    @ColumnInfo(name = "documentId")
    var documentId: String = ""

}