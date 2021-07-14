package com.efsanetarih.wordrivaladmin.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.efsanetarih.wordrivaladmin.AppDatabase
import com.efsanetarih.wordrivaladmin.model.Question
import com.google.firebase.firestore.FirebaseFirestore

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _mAllQuestions = MutableLiveData<List<Question?>?>()
    val mAllQuestions: LiveData<List<Question?>?>?
        get() = _mAllQuestions
    val db: AppDatabase =
        AppDatabase.getAppDatabase(application.applicationContext)!!

    private var myMainViewModel: MainViewModel? = null

    var mAllWords: LiveData<List<Question?>?>? = null

    fun getWord(): LiveData<List<Question?>?>? {
        mAllWords = db.questionDao()?.getWord
        return mAllWords
    }

    fun addQuestionSql(question: Question?) {
        db.questionDao()?.insertAll(question)

    }
    fun deleteQuestionSql() {
        db.questionDao()?.deleteAllQuestions()

    }

    fun addQuestionFirebase(data: Question) {
        fireStore.collection("Questions").document()
            .set(data)
            .addOnSuccessListener { Log.d("TAGFirebase", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("TAGFirebase", "Error writing document", e) }
    }


    init {
        _mAllQuestions.value = arrayListOf(Question())
    }

    fun getQuestion() {
        fireStore.collection("Questions")
            //.whereEqualTo("", true)
            .get()
            .addOnSuccessListener { documents ->
                val data = documents.toObjects(Question::class.java)
                data.forEachIndexed{index,question ->
                    question.documentId = documents.documents[index].id
                }
                _mAllQuestions.postValue(data)

                Log.d("TAG", "test")

            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }
    }
}