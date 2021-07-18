package com.efsanetarih.wordrivaladmin.view

import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.efsanetarih.wordrivaladmin.AppDatabase
import com.efsanetarih.wordrivaladmin.R
import com.efsanetarih.wordrivaladmin.adapter.QuestionAdapter
import com.efsanetarih.wordrivaladmin.model.Question
import com.efsanetarih.wordrivaladmin.databinding.FragmentMainBinding
import com.efsanetarih.wordrivaladmin.viewmodel.MainViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class MainFragment : Fragment(), QuestionAdapter.OnItemClickListener {
    private var vModel: MainViewModel? = null
    private lateinit var binding: FragmentMainBinding


    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater)
        val db: AppDatabase =
            AppDatabase.getAppDatabase(requireContext())!!

        db.questionDao()?.getWord?.observe(viewLifecycleOwner, observerSql)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        vModel?.getQuestion()
        vModel!!.mAllQuestions?.observe(viewLifecycleOwner, observerFirebasedenAlıpSqleKaydet)

        binding.btnAddQuestion.setOnClickListener {
            showDialog()
        }


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    fun displayAllWords() {
        val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
        fireStore.collection("Questions")
            //.whereEqualTo("", true)
            .get()
            .addOnSuccessListener { documents ->
                val data: List<Question?> = documents.toObjects(Question::class.java)
                val adapter = QuestionAdapter(requireContext(), data, this)
                binding.recyclerView.adapter = adapter
                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            }

            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }


    }

    private fun getText(data: Any): String {
        var str = ""
        if (data is EditText) {
            str = data.text.toString()
        } else if (data is String) {
            str = data
        }
        return str
    }

    fun validation(data: Any, updateUI: Boolean = true): Boolean {
        val str = getText(data)
        var valid = true

        val exp = ".[~!@#\$%\\^&()\\-_=+\\|\\[{\\]};:'\",<.>/?].*"
        val exp2 = "\\str"
        val pattern = Pattern.compile(exp)
        val pattern2 = Pattern.compile(exp2)
        val matcher = pattern.matcher(str)
        val matcher2 = pattern2.matcher(str)
        if (matcher.matches()) {
            valid = false
        }
        if (matcher2.matches()) {
            valid = false
        }
        return valid
    }

    private fun showDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.customdialog_addquestion)
        val wordEn = dialog.findViewById<EditText>(R.id.edt_wordEn)
        val wordTr = dialog.findViewById<EditText>(R.id.edt_wordTr)
        val falseAnswer1 = dialog.findViewById<EditText>(R.id.answerFalse3)
        val falseAnswer2 = dialog.findViewById<EditText>(R.id.answerFalse1)
        val falseAnswer3 = dialog.findViewById<EditText>(R.id.answerFalse2)
        val btn_addWords = dialog.findViewById<Button>(R.id.btn_addWord)

        btn_addWords.setOnClickListener {
            /*        val Tr = edtWordTr.text.toString()
                    val En = edtWordEn.text.toString()
                    val A1 = falseAnswer1.text.toString()
                    val A2 = falseAnswer2.text.toString()
                    val A3 = falseAnswer3.text.toString()
        */
            val word: Question = Question()//constructor
            word.word = wordEn.text.toString()//ingilizce kelimeleri string dönüştürme
            word.answerTrue = wordTr.text.toString()//türkçe kelimeleri string dönüştürme
            word.answerFalse1 = falseAnswer1.text.toString()
            word.answerFalse2 = falseAnswer2.text.toString()
            word.answerFalse3 = falseAnswer3.text.toString()

            val validating =
                validation(wordEn) && validation(wordTr) && validation(falseAnswer1) && validation(
                    falseAnswer2
                ) && validation(falseAnswer3)

            if (validating) {
                vModel!!.addQuestionFirebase(word)
                vModel?.addQuestionSql(word)
                dialog.dismiss()
                displayAllWords()


            } else {
                Toast.makeText(
                    requireContext(),
                    "Lütfen uygun karakterleri kullanın",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        dialog.show()
    }

    override fun onItemClick(position: Int, data: Question?) {
        showQuestionContext(data)
    }

    private val observerFirebasedenAlıpSqleKaydet = Observer { getData: List<Question?>? ->
        if (getData?.size!! > 1) {
            vModel?.deleteQuestionSql()
            for(question in getData){
                vModel?.addQuestionSql(question)

            }
            //loadData(getData)//showing data from
        }
    }
    private val observerSql = Observer { getData: List<Question?> ->
        if (getData?.size!! > 1) {
            loadData(getData)
        }
    }

    private fun loadData(data: List<Question?>) {
        val adapter = QuestionAdapter(requireContext(), data, this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showQuestionContext(data: Question?) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.customdialog_showquestion)

        val word = dialog.findViewById<TextView>(R.id.txt_word)
        val answerTrue = dialog.findViewById<EditText>(R.id.txt_answerTrue)
        val falseAnswer1 = dialog.findViewById<EditText>(R.id.txt_answerFalse1)
        val falseAnswer2 = dialog.findViewById<EditText>(R.id.txt_answerFalse2)
        val falseAnswer3 = dialog.findViewById<EditText>(R.id.txt_answerFalse3)
        val update = dialog.findViewById<Button>(R.id.btn_update)


        word.text = data?.word.toString()//textview da böyle yazılıyor ekrana
        answerTrue.setText(data?.answerTrue.toString())//edittextte böyle yazdırılıyor
        falseAnswer1.setText(data?.answerFalse1.toString())
        falseAnswer2.setText(data?.answerFalse2.toString())
        falseAnswer3.setText(data?.answerFalse3.toString())

        update.setOnClickListener {
            updateData(answerTrue, falseAnswer1, falseAnswer2, falseAnswer3, data?.word.toString())
            dialog.dismiss()
        }
        dialog.show()
    }

    fun updateData(
        answerTrue: EditText,
        falseAnswer1: EditText,
        falseAnswer2: EditText,
        falseAnswer3: EditText,
        wordValue: String
    ) {

        FirebaseFirestore.getInstance().collection("Questions")
            .whereEqualTo("word", wordValue).get()
            .addOnCompleteListener(
                OnCompleteListener {
                    if (it.isSuccessful) {
                        for (document in it.result!!) {
                            val ref = FirebaseFirestore.getInstance()
                                .collection("Questions")
                                .document(document.id)

                            val updates = hashMapOf<String, Any>(
                                "answerTrue" to answerTrue.text.toString(),
                                "answerFalse1" to falseAnswer1.text.toString(),
                                "answerFalse2" to falseAnswer2.text.toString(),
                                "answerFalse3" to falseAnswer3.text.toString()
                            )

                            ref.update(updates)
                                .addOnSuccessListener {
                                    Log.d(
                                        "TAG",
                                        "DocumentSnapshot successfully updated!"
                                    )

                                    vModel?.getQuestion()

                                }
                                .addOnFailureListener { e ->
                                    Log.w(
                                        "TAG",
                                        "Error updating document",
                                        e
                                    )
                                }

                        }
                    } else {
                        Log.d("TAG", "Error getting documents: ", it.exception);
                    }
                })
    }
}





