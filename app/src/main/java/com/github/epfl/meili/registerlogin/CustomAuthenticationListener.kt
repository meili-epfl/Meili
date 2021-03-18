package com.github.epfl.meili.registerlogin

interface CustomAuthenticationListener {
    fun logInSuccess(email: String, password: String)
    fun logInFailure(exception: Exception, email: String, password: String)
}