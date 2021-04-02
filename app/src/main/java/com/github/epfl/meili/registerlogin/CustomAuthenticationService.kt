package com.github.epfl.meili.registerlogin

import androidx.appcompat.app.AppCompatActivity

interface CustomAuthenticationService {

    fun init()

    fun createUser(activity: AppCompatActivity, email: String, password: String, username: String)


    fun signInWithEmailAndPassword(activity: AppCompatActivity, email: String, password: String)

}
