package ch.mse.quiz.firebase

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.quiz.firebase.FirebaseLogin
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

class LoginLogic(val auth:FirebaseAuth, val loginInstance: FirebaseLogin): OnCompleteListener<AuthResult> {
    override fun onComplete(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            // Sign in success, update UI with the signed-in user's information
            //Log.d(loginInstance.TAG, "createUserWithEmail:success")
            val user = auth.currentUser
            val returnIntent = Intent()
            val result = user?.email
            returnIntent.putExtra("result", result)
            loginInstance.setResult(Activity.RESULT_OK, returnIntent)
        } else {
            // If sign in fails, display a message to the user.
            //Log.w(loginInstance.FirebaseLogin.TAG, "createUserWithEmail:failure", task.exception)
            Toast.makeText(loginInstance.baseContext, "Account creation failed.",
                    Toast.LENGTH_SHORT).show()
        }
    }


}