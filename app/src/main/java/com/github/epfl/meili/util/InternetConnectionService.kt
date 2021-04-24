package com.github.epfl.meili.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.github.epfl.meili.MainApplication

open class InternetConnectionService {
    open fun isConnectedToInternet(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
}