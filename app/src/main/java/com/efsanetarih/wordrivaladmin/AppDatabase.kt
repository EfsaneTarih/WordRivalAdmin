package com.efsanetarih.wordrivaladmin

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.efsanetarih.wordrivaladmin.model.Question
import com.efsanetarih.wordrivaladmin.database.QuestionDao


@Database(entities = [Question::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao?

    companion object {
        private var INSTANCE: AppDatabase? = null
        fun getAppDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase::class.java, "user-database"
                )
                    .allowMainThreadQueries()
                    .build()
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }

    }

}