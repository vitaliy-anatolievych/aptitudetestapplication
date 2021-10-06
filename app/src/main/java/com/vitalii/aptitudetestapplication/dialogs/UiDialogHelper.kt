package com.vitalii.aptitudetestapplication.dialogs

import com.google.firebase.auth.FirebaseUser
import com.vitalii.aptitudetestapplication.MapsActivity
import com.vitalii.aptitudetestapplication.R

class UiDialogHelper(private val activity: MapsActivity) {
    private val loginInButton = activity.binding.navView.menu.findItem(R.id.login_in)
    private val registrationButton = activity.binding.navView.menu.findItem(R.id.registration)
    private val signOutButton = activity.binding.navView.menu.findItem(R.id.sign_out)

    fun uiUpdate(user: FirebaseUser?) {
        activity.userEmailHeader.text = if (user == null) {
            activity.resources.getString(R.string.header_guest_email_text)
        } else {
            user.email
        }

        if (user == null) {
            hideNavigationButtons()
            loginInButton.isEnabled = true
            registrationButton.isEnabled = true
        } else {
            hideNavigationButtons()
            signOutButton.isEnabled = true
        }
    }

    private fun hideNavigationButtons() {
        loginInButton.isEnabled = false
        registrationButton.isEnabled = false
        signOutButton.isEnabled = false
    }
}