package ch.mse.quiz.firebase

import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import ch.mse.quiz.question
import com.example.quiz.firebase.FirebaseLogin
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*


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

    fun getQuestions(topic: String):String {
        var mystring ="nothing"
        val list = mutableListOf<String>()
        val dbref = database.getReference("geography/questions/1/question")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                Log.d("dbTag","befor")
                val post = dataSnapshot.getValue(String::class.java)
                mystring = post.toString()
                mystring = "failed"
                /*Log.d("dbTag","after")
                if (post != null) {
                    Log.d("dbTag",post)
                    list.add(post)
                } else {
                    Log.d("dbTag","its null")
                }*/

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("mytag", "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        dbref.addListenerForSingleValueEvent(postListener)
        Log.w("mytag", "test")
        return mystring

    }
}