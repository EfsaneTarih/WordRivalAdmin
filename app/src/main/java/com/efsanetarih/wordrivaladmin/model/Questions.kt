package com.efsanetarih.wordrivaladmin.model

data class Questions(
    val answerTrue: String,
    val word: String,
    val answerFalse1: String,
    val answerFalse2: String,
    val answerFalse3: String,
    val type: Int,
    val level: Int,
    val user1Answer: Int,
    val user2Answer: Int
) {

    constructor() : this("", "", "", "", "", 1, 1, 0, 0)
}
