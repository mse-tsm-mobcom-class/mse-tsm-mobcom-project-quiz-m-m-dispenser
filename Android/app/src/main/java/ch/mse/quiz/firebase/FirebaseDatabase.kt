package ch.mse.quiz.firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import ch.mse.quiz.R
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseDatabase : AppCompatActivity() {
    // Write a message to the database
    val database = Firebase.database
    val myRef = database.getReference("message")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_database)
    }

    fun writeQuestion(view: View){
        val obj = {

        }
        myRef.setValue("Hello, World!")
    }




}