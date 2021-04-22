package com.github.epfl.meili.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class InternetConnectionServiceTest {
    @Test
    fun isConnectedToInternetTrueTest(){
        val mockActiveNetwork = Mockito.mock(NetworkInfo::class.java)
        Mockito.`when`(mockActiveNetwork.isConnectedOrConnecting).thenReturn(true)
        val mockConnectivityManager = Mockito.mock(ConnectivityManager::class.java)
        Mockito.`when`(mockConnectivityManager.activeNetworkInfo).thenReturn(mockActiveNetwork)
        val mockContext = Mockito.mock(Context::class.java)
        Mockito.`when`(mockContext.getSystemService(Mockito.anyString())).thenReturn(mockConnectivityManager)
        assertEquals(InternetConnectionService.isConnectedToInternet(mockContext), true)
    }

    @Test
    fun isConnectedToInternetFalseTest(){
        val mockActiveNetwork = Mockito.mock(NetworkInfo::class.java)
        Mockito.`when`(mockActiveNetwork.isConnectedOrConnecting).thenReturn(false)
        val mockConnectivityManager = Mockito.mock(ConnectivityManager::class.java)
        Mockito.`when`(mockConnectivityManager.activeNetworkInfo).thenReturn(mockActiveNetwork)
        val mockContext = Mockito.mock(Context::class.java)
        Mockito.`when`(mockContext.getSystemService(Mockito.anyString())).thenReturn(mockConnectivityManager)
        assertEquals(InternetConnectionService.isConnectedToInternet(mockContext), false)
    }
}