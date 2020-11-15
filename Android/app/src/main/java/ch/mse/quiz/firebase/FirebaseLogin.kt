package com.example.quiz.firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.example.quiz.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase

class FirebaseLogin : AppCompatActivity() {
    companion object {
        private val TAG = "Firebase"
    }
    // TESTUSER
    val email = "testuser@testdomain.com"
    val password = "password"
    lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_login)
        auth = Firebase.auth
    }


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }


    private fun validateForm(): Boolean {
        var valid = true

        val email = findViewById<EditText>(R.id.fieldEmail).toString()
        if (TextUtils.isEmpty(email)) {
            findViewById<EditText>(R.id.fieldEmail).setText("required")
            valid = false
        }
        val password = R.id.fieldPassword.toString()
        if (TextUtils.isEmpty(password)) {
            findViewById<EditText>(R.id.fieldPassword).setText("required")
            valid = false
        }
        return valid
    }

    fun signIn(view: View) {
        val email = findViewById<EditText>(R.id.fieldEmail).text.toString()
        val password = findViewById<EditText>(R.id.fieldPassword).text.toString()
        Log.d(TAG, "signIn:$email")
        if (!validateForm()) {
            return
        }
        //showProgressBar()
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                    if (!task.isSuccessful) {
                        findViewById<EditText>(R.id.status).setText(R.string.auth_failed)
                    }
                    //hideProgressBar()
                }
    }


    private fun updateUI(user: FirebaseUser?) {
        //hideProgressBar()
        if (user != null) {
            findViewById<EditText>(R.id.status).setText(" ${user.email.toString()} ${user.isEmailVerified.toString()}")
            findViewById<EditText>(R.id.detail).setText(user.uid.toString())
            //findViewById<LinearLayout>(R.id.emailPasswordButtons).visibility = View.GONE
            //findViewById<LinearLayout>(R.id.emailPasswordFields).visibility = View.GONE
            findViewById<Button>(R.id.emailSignInButton).visibility = View.VISIBLE
        } else {
            findViewById<EditText>(R.id.status).setText(R.string.signed_out)
            findViewById<EditText>(R.id.detail).setText(" useris is null")
            findViewById<LinearLayout>(R.id.emailPasswordButtons).visibility = View.VISIBLE
            findViewById<LinearLayout>(R.id.emailPasswordFields).visibility = View.VISIBLE
            //findViewById<Button>(R.id.emailSignInButton).visibility = View.GONE
        }
    }
}