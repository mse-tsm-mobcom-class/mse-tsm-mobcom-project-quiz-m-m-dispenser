package ch.mse.quiz.firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import ch.mse.quiz.R
import ch.mse.quiz.question
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseDatabase : AppCompatActivity() {
    val database = Firebase.database
    val myRef = database.reference
    val auth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_database)
    }

    fun storeQuestion(view: View) {
        // Write a message to the database
        val user = auth.getCurrentUser()
        val obj = question("Does this work?2", 2, arrayOf<String>("yes", "no", "maybe"))
        val useruid = user?.getUid()
        if (useruid != null) {
            myRef.child(useruid).setValue(obj)
        } else {
            Toast.makeText(baseContext, "Authentication  of useruid failed.",
                    Toast.LENGTH_SHORT).show()
        }
    }
}