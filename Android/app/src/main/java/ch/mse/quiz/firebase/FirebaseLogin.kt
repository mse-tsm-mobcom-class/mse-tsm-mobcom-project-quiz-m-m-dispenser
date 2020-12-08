// Copyright (c) 2020, Steiner Pascal, Strässle Nikolai, Radinger Martin
// All rights reserved.

// Licensed under LICENSE, see LICENSE file

package com.example.quiz.firebase

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ch.mse.quiz.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth


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

        val email = findViewById<TextInputEditText>(R.id.fieldEmail)
        if (TextUtils.isEmpty(email.text) || (email.text.toString() == "required")) {
            email.setText("required")

            valid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            email.setText("please enter valid email")

            valid = false
        }

        val password = findViewById<TextInputEditText>(R.id.fieldPassword)
        if (TextUtils.isEmpty(password.text) || (password.text.toString() == "required")) {
            password.setText("required")
            password.transformationMethod = PasswordTransformationMethod.getInstance()

            valid = false
        } else if ((password.text.toString().length < 5) || (password.text.toString() == "password too short")) {
            password.setText("password too short")
            password.transformationMethod = PasswordTransformationMethod.getInstance()

            valid = false
        }
        return valid
    }

    fun createAccount(view: View) {
        val email = findViewById<TextInputEditText>(R.id.fieldEmail).text.toString()
        val password = findViewById<TextInputEditText>(R.id.fieldPassword).text.toString()
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

    fun signIn(view: View) {
        val email = findViewById<TextInputEditText>(R.id.fieldEmail).text.toString()
        val password = findViewById<TextInputEditText>(R.id.fieldPassword).text.toString()
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