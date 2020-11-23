package com.example.quiz.firebase

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ch.mse.quiz.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class FirebaseLogin : AppCompatActivity() {
    companion object {
        private val TAG = "Firebase"
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_login)
        // init firebase auth and get user
        auth = FirebaseAuth.getInstance()
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

    fun createAccount(view: View) {
        val email = findViewById<EditText>(R.id.fieldEmail).text.toString()
        val password = findViewById<EditText>(R.id.fieldPassword).text.toString()
        Log.d(TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        val returnIntent = Intent()
                        val result = user?.email
                        returnIntent.putExtra("result", result)
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Account creation failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
        }

    fun signIn(view: View){
        val email = findViewById<EditText>(R.id.fieldEmail).text.toString()
        val password = findViewById<EditText>(R.id.fieldPassword).text.toString()
        Log.d(TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        val returnIntent = Intent()
                        val result = user?.email
                        returnIntent.putExtra("result", result)
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }

}