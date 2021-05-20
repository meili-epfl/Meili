package com.facebook.login

import com.facebook.AccessTokenCreator.createToken
import com.facebook.login.LoginClient.Request
import com.facebook.login.LoginClient.Result

internal object LoginClientCreator {

    private fun createRequest(): Request {
        val permissions: HashSet<String> =
            hashSetOf("user_actions.music", "user_friends", "user_likes", "email")
        return Request(
            LoginBehavior.NATIVE_WITH_FALLBACK,
            permissions,
            DefaultAudience.EVERYONE,
            "authType",
            "appId",
            "authId"
        )
    }

    /**
     * Used for testing the facebook sign in, creates a login result
     */
    fun createResult(): Result {
        val request: Request = createRequest()
        return Result(request, Result.Code.SUCCESS, createToken(request.permissions), null, null)
    }
}