package com.github.epfl.meili.notification

import com.github.epfl.meili.models.NotificationData.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Retrofit is a package that helps communicate with the web,
 * this class helps instantiate it with its builder using
 * our desired default values.
 */
class RetrofitInstance {

    companion object{
        //only need to initialize this variable when needed
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        }

        val api by lazy{
            retrofit.create(NotificationAPI::class.java)
        }
    }
}