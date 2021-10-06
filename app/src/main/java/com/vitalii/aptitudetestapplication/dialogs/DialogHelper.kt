package com.vitalii.aptitudetestapplication.dialogs

import android.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.vitalii.aptitudetestapplication.MapsActivity
import com.vitalii.aptitudetestapplication.R
import com.vitalii.aptitudetestapplication.accounts.AccountHelper
import com.vitalii.aptitudetestapplication.databinding.SignDialogBinding

class DialogHelper(private val activity: MapsActivity) {
    val accountHelper = AccountHelper(activity)

    fun createSignDialog(typeDialog: DialogType) {
        val builder = AlertDialog.Builder(activity)
        val rootDialog = SignDialogBinding.inflate(activity.layoutInflater)
        val view = rootDialog.root
        builder.setView(view)

        setDialogState(typeDialog, rootDialog)

        val dialog = builder.create()
        rootDialog.btSignUpIn.setOnClickListener {
            setOnClickSignUpIn(typeDialog, rootDialog, dialog)
        }
        rootDialog.btGoogleSignIn.setOnClickListener {
            accountHelper.signInWithGoogle()
            dialog.dismiss()
        }
        rootDialog.btForgetPassword.setOnClickListener {
            setOnClickResetPassword(rootDialog, dialog)
        }
        dialog.show()
    }

    private fun setOnClickResetPassword(rootDialog: SignDialogBinding, dialog: AlertDialog?) {
        val email = rootDialog.edSignEmail.text

        if (email.isNotEmpty()) {
            activity.mAuth.sendPasswordResetEmail(email.toString()).addOnCompleteListener {
                task->
                if (task.isSuccessful) {
                    Toast.makeText(activity, R.string.email_reset_password_was_send, Toast.LENGTH_LONG).show()
                }
            }
            dialog?.dismiss()
        } else {
            rootDialog.tvDialogMessage.text = activity.resources.getString(R.string.dialog_reset_email_message)
            rootDialog.tvDialogMessage.visibility = View.VISIBLE

        }
    }

    private fun setOnClickSignUpIn(typeDialog: DialogType, rootDialog: SignDialogBinding, dialog: AlertDialog) {
        val email = rootDialog.edSignEmail.text.toString()
        val password = rootDialog.edSignPassword.text.toString()

        when(typeDialog) {
            DialogType.SIGN_UP_DIALOG -> {
                accountHelper.signUpWithEmail(email, password, dialog)
            }
            DialogType.SIGN_IN_DIALOG -> {
                accountHelper.signInWithEmail(email, password, dialog, rootDialog)
            }
        }
    }

    private fun setDialogState(typeDialog: DialogType, rootDialog: SignDialogBinding) {
        when(typeDialog) {
            DialogType.SIGN_UP_DIALOG -> {
                rootDialog.tvSignTitle.text = activity.resources.getString(R.string.user_sign_up)
                rootDialog.btSignUpIn.text = activity.resources.getString(R.string.sign_up_action)
                rootDialog.btForgetPassword.visibility = View.GONE
            }
            DialogType.SIGN_IN_DIALOG -> {
                rootDialog.tvSignTitle.text = activity.resources.getString(R.string.user_login_in)
                rootDialog.btSignUpIn.text = activity.resources.getString(R.string.sign_in_action)
                rootDialog.btForgetPassword.visibility = View.VISIBLE
            }
        }
    }
}