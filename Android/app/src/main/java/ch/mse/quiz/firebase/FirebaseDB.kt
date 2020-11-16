package ch.mse.quiz.firebase

import android.view.View
import android.widget.Toast
import ch.mse.quiz.question
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseDB {

    val database = Firebase.database
    val myRef = database.reference
    val auth = Firebase.auth

    fun storeUserScore(score: Int): Boolean {
        // Write a message to the database
        val user = auth.getCurrentUser()
        val useruid = user?.getUid()
        if (useruid != null) {
            myRef.child(useruid).setValue(score)
            return true
        } else {
            return false
        }
    }

    fun getQuestions(topic: String) {
        val data = myRef.child("geography")
        print(data)
    }
}