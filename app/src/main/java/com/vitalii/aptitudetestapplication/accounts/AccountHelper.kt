package com.vitalii.aptitudetestapplication.accounts

import android.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import com.vitalii.aptitudetestapplication.MapsActivity
import com.vitalii.aptitudetestapplication.R
import com.vitalii.aptitudetestapplication.constants.FirebaseAuthConstants
import com.vitalii.aptitudetestapplication.databinding.SignDialogBinding

class AccountHelper(private val activity: MapsActivity) {
    private lateinit var signInClient: GoogleSignInClient

    fun signInFirebaseWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        activity.mAuth.signInWithCredential(credential).addOnCompleteListener {
            task->
            if (task.isSuccessful) {
                Toast.makeText(activity, R.string.google_sign_in_done, Toast.LENGTH_LONG).show()
                activity.uiDialogHelper.uiUpdate(task.result?.user)
            } else {
                Log.d("AuthExceptions", "signInFirebaseWithGoogle exception: ${task.exception}")
            }
        }
    }

    fun signInWithGoogle() {
        signInClient = getSignInClient()
        val intent = signInClient.signInIntent
        activity.startActivityForResult(intent, GoogleAccountConst.GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    fun signOutGoogle() {
        getSignInClient().signOut()
    }

    fun signUpWithEmail(email: String, password: String, dialog: AlertDialog) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            activity.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                task ->
                if (task.isSuccessful) {
                    sendEmailVerification(task.result?.user!!)
                    activity.uiDialogHelper.uiUpdate(task.result?.user)
                    dialog.dismiss()
                } else {
//                    Toast.makeText(activity, activity.resources.getString(R.string.sign_up_error), Toast.LENGTH_LONG).show()
                        Log.d("AuthExceptions", "signUpWithEmail exception: ${task.exception}")
//                        Log.d("AuthExceptions", "Auth exception: ${exception.errorCode}")
                    when(task.exception) {
                        is FirebaseAuthUserCollisionException -> {
                            val exception = task.exception as FirebaseAuthUserCollisionException
                            Log.d("AuthExceptions", "Auth exception: ${exception.errorCode}")
                            if (exception.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                                Toast.makeText(activity, activity.resources.getString(R.string.error_email_already_in_use), Toast.LENGTH_LONG).show()
//                                linkEmailToG(email, password)
                            }
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            val exception = task.exception as FirebaseAuthInvalidCredentialsException
                            Log.d("AuthExceptions", "Auth exception: ${exception.errorCode}")
                            if (exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                                Toast.makeText(activity, activity.resources.getString(R.string.error_invalid_email), Toast.LENGTH_LONG).show()
                            } else if (exception.errorCode == FirebaseAuthConstants.ERROR_WEAK_PASSWORD) {
                                Toast.makeText(activity, activity.resources.getString(R.string.error_weak_password), Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        } else {
            Toast.makeText(activity, activity.resources.getText(R.string.login_or_password_empty), Toast.LENGTH_SHORT).show()
        }
    }


    fun signInWithEmail(email: String, password: String, dialog: AlertDialog, rootDialog: SignDialogBinding) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            activity.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    task ->
                if (task.isSuccessful) {
                    activity.uiDialogHelper.uiUpdate(task.result?.user)
                    dialog.dismiss()
                } else {
                    Log.d("AuthExceptions", "signInWithEmail exception: ${task.exception}")
                    rootDialog.tvDialogMessage.text = activity.resources.getString(R.string.sign_in_error)
                    rootDialog.tvDialogMessage.visibility = View.VISIBLE
                }
            }
        } else {
            rootDialog.tvDialogMessage.text = activity.resources.getString(R.string.login_or_password_empty)
            rootDialog.tvDialogMessage.visibility = View.VISIBLE
        }
    }


    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener {
            task ->
            if (task.isSuccessful) {
                Toast.makeText(activity, activity.resources.getString(R.string.send_verification_email_done), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(activity, activity.resources.getString(R.string.send_verification_email_error), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getSignInClient(): GoogleSignInClient {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id)).requestEmail().build()


        return GoogleSignIn.getClient(activity ,googleSignInOptions)
    }
}