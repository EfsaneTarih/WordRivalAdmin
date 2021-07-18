package com.efsanetarih.wordrivaladmin.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.efsanetarih.wordrivaladmin.R
import com.efsanetarih.wordrivaladmin.model.Question
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

class QuestionAdapter(
    private var c: Context, var questions: List<Question?>, val listener: OnItemClickListener
) : RecyclerView.Adapter<QuestionAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QuestionAdapter.MyViewHolder {
        val view: View = LayoutInflater.from(c).inflate(R.layout.item_question, parent, false)

        return MyViewHolder(view)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.word.text = questions[position]?.word
        holder.answerTrue.text = questions[position]?.answerTrue

        holder.btn_delete.setOnClickListener {
            deleteQuestionContext(questions[position]?.documentId!!)
        }
    }

    override fun getItemCount(): Int {
        return questions!!.size
    }

    private fun deleteQuestionContext(documentId: String) {
        val dialogClickListener =
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {

                        FirebaseFirestore.getInstance().collection("Questions")
                            .whereEqualTo(FieldPath.documentId(), documentId).get()
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    for (document in it.result!!) {
                                        FirebaseFirestore.getInstance().collection("Questions")
                                            .document(document.id).delete()
                                        val listofyedek: ArrayList<Question?> =
                                            arrayListOf() // arraylist kullanarak liste yaratmak
                                        for (i in questions!!) {
                                            if (i?.documentId != documentId) {
                                                listofyedek.add(i)
                                            }
                                        }
                                        questions = listofyedek
                                        notifyDataSetChanged()
                                    }
                                } else {
                                    Log.d("TAG", "Error getting documents: ", it.exception)
                                }

                            }

                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                    }
                }
            }
        val ab = AlertDialog.Builder(c)

        ab.setTitle("UYARI")
        ab.setMessage("Kelimeyi Silmek İstediğinizden Emin Misiniz?")
            .setPositiveButton("Evet", dialogClickListener)
            .setNegativeButton("Hayır", dialogClickListener).show()



        notifyDataSetChanged()
    }


    inner class MyViewHolder(questionView: View) : RecyclerView.ViewHolder(questionView),
        View.OnClickListener {
        val word = questionView.findViewById<TextView>(R.id.word)
        val answerTrue = questionView.findViewById<TextView>(R.id.answerTrue)
        val btn_delete = questionView.findViewById<ImageButton>(R.id.btn_deleteWord)

        init {
            questionView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {//validation of position item
                listener.onItemClick(position, questions?.get(position))
            }
        }

    }


    interface OnItemClickListener {
        fun onItemClick(position: Int, data: Question?)

    }


}




