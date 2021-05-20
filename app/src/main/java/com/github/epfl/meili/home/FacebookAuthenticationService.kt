package com.github.epfl.meili.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.Profile
import com.facebook.login.LoginManager
import com.github.epfl.meili.models.User
import org.json.JSONException
import org.json.JSONObject

class FacebookAuthenticationService : AuthenticationService {

    companion object {
        fun fetchFacebookUser(accessToken: AccessToken?, profile: Profile): User? {
            if (accessToken == null) {
                return null
            } else {
                var email = ""

                val request = GraphRequest.newMeRequest(
                    accessToken
                ) { jsonObject: JSONObject, _: GraphResponse ->
                    try {
                        email = jsonObject.getString("email")
                    } catch (e: JSONException) {
                    }
                }

                val parameters = Bundle()
                parameters.putString("fields", "email")
                request.parameters = parameters

                val t = Thread(request::executeAndWait)
                t.start()
                t.join()

                return User(profile.id, profile.name, email, " ")
            }
        }
    }

    override fun getCurrentUser(): User? {
        return fetchFacebookUser(AccessToken.getCurrentAccessToken(), Profile.getCurrentProfile())
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
        onComplete: () -> Unit,
    ) {
    }
}