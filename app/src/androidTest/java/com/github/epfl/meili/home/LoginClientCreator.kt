package com.github.epfl.meili.home

//import com.facebook.login.DefaultAudience
//import com.facebook.login.LoginBehavior
//import com.facebook.*
//
//
//internal object LoginClientCreator {
//
//    fun createRequest(): Request {
//        val permissions: HashSet<String> = hashSetOf("user_actions.music", "user_friends", "user_likes", "email")
//        return Request(LoginBehavior.NATIVE_WITH_FALLBACK, permissions, DefaultAudience.EVERYONE, "authType", "appId", "authId")
//    }
//
//    fun createResult(): Result? {
//        val request: Request = createRequest()
//        return Result(request, Result.Code.SUCCESS, createToken(request.permissions), null, null)
//    }
//}