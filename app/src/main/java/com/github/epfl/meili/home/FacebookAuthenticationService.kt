package com.github.epfl.meili.home

import android.app.Activity
import android.content.Intent
import com.facebook.AccessToken
import com.facebook.Profile
import com.facebook.login.LoginManager
import com.github.epfl.meili.models.User

class FacebookAuthenticationService : AuthenticationService {
    private var profile: Profile? = null
    private var accessToken: AccessToken? = null

    override fun getCurrentUser(): User? {
        if (accessToken == null)
            accessToken = AccessToken.getCurrentAccessToken()
        val loggedOut = accessToken == null
        return if (loggedOut) {
            null
        } else {
            fetchUser()
        }
    }

    /**
     * Used to set the profile, used in tests
     */
    fun setProfile(profile: Profile) {
        this.profile = profile
    }

    /**
     * Used to set the token, used in tests
     */
    fun setAccessToken(accessToken: AccessToken?) {
        this.accessToken = accessToken
    }

    private fun fetchUser(): User {
        if (profile == null)
            profile = Profile.getCurrentProfile()

        return User(profile!!.id, profile!!.name, "", " ")
    }

    override fun signInIntent(): Intent {
        return Intent()
    }

    override fun signOut() {
        LoginManager.getInstance().logOut()
        Auth.setAuthenticationService(FirebaseAuthenticationService())
    }

    override fun onActivityResult(
        activity: Activity,
        requestCode: Int,
        result: Int,
        data: Intent?,
        onComplete: () -> Unit
    ) {
    }
}