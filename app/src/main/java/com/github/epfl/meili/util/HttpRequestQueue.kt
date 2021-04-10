package com.github.epfl.meili.util

import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.github.epfl.meili.MainApplication

object HttpRequestQueue {
    private var queue: RequestQueue? = null

    fun getQueue(): RequestQueue {
        if (queue == null) {
            queue = Volley.newRequestQueue(MainApplication.applicationContext())
        }

        return queue!!
    }
}