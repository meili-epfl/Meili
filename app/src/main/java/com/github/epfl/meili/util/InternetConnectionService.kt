package com.github.epfl.meili.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.github.epfl.meili.MainApplication

object InternetConnectionService {
    fun isConnectedToInternet(): Boolean {
        val cm = MainApplication.applicationContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
}