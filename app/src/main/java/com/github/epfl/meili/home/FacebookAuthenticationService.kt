package com.github.epfl.meili.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.facebook.*
import com.facebook.login.LoginManager
import com.github.epfl.meili.models.User
import org.json.JSONException
import org.json.JSONObject

class FacebookAuthenticationService : AuthenticationService {
    override fun getCurrentUser(): User? {
        val loggedOut = AccessToken.getCurrentAccessToken() == null
        return if (loggedOut) {
            null
        } else {
            fetchUser()
        }
    }

    private fun fetchUser(): User {
        val profile = Profile.getCurrentProfile()
        var email = ""
//
//        val request = GraphRequest.newMeRequest(
//            AccessToken.getCurrentAccessToken()
//        ) { jsonObject: JSONObject, _: GraphResponse ->
//            try {
//                email = jsonObject.getString("email")
//            } catch (e: JSONException) {
//            }
//        }
//
//        val parameters = Bundle()
//        parameters.putString("fields", "email")
//        request.parameters = parameters
//
//        val t = Thread(request::executeAndWait)
//        t.start()
//        t.join()

        return User(profile.id, profile.name, email, " ")
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