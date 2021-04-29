package com.github.epfl.meili.cache

import android.content.SharedPreferences
import androidx.test.espresso.core.internal.deps.guava.cache.Cache
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.epfl.meili.MainApplication
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.poi.PoiGoogleRetriever
import com.github.epfl.meili.poi.PoiServiceCached
import com.github.epfl.meili.util.InternetConnectionService
import com.google.android.gms.maps.model.LatLng
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import java.lang.reflect.Type

@RunWith(AndroidJUnit4::class)
class CacheServiceTest {
    private val type: Type = object: TypeToken<List<PointOfInterest?>?>() {}.type
    private val service: CacheService<List<PointOfInterest>> = CacheService("MOCK_PREFERENCES", type)
    private var mockInternetConnectionService: InternetConnectionService = Mockito.mock(InternetConnectionService::class.java)
    private var mockSharedPreferences: SharedPreferences = Mockito.mock(SharedPreferences::class.java)
    private var mockSharedPreferencesEditor: SharedPreferences.Editor = Mockito.mock(SharedPreferences.Editor::class.java)
    private val mockPoiGoogleRetriever: PoiGoogleRetriever = Mockito.mock(PoiGoogleRetriever::class.java)

    private val testPoiList = ArrayList<PointOfInterest>()
    private val poi1 = PointOfInterest(41.075000, 1.130870, "place1", "place1")
    private val poi2 = PointOfInterest(41.063563, 1.083658, "place2", "place2")
    private val testPosition = LatLng(43.0, 1.1)

    @Before
    fun initMocks() {
        testPoiList.add(poi1)
        testPoiList.add(poi2)

        Mockito.`when`(mockSharedPreferencesEditor.putLong(Mockito.anyString(), Mockito.anyLong())).thenReturn(null)
        Mockito.`when`(mockSharedPreferences.edit()).thenReturn(mockSharedPreferencesEditor)
    }

    private fun initEmptyPreferences() {
        Mockito.`when`(mockSharedPreferences.getLong(Mockito.anyString(), Mockito.anyLong())).thenReturn(0L)
        service.setSharedPreferences(mockSharedPreferences)
    }

    private fun initPreferencesWithData(timestamp: Long) {
        Mockito.`when`(mockSharedPreferences.getLong(Mockito.anyString(), Mockito.anyLong())).then {
            val key = it.arguments[0] as String
            if (key == CacheService.TIMESTAMP_KEY) {
                return@then timestamp
            } else {
                return@then null
            }
        }
        Mockito.`when`(mockSharedPreferences.getString(Mockito.anyString(), Mockito.anyString())).then {
            val key = it.arguments[0] as String
            if (key == CacheService.RESPONSE_KEY) {
                return@then Gson().toJson(testPoiList, testPoiList::class.java)
            } else {
                return@then null
            }
        }

        service.setSharedPreferences(mockSharedPreferences)
    }

    private fun setInternetConnection(status: Boolean) {
        Mockito.`when`(mockInternetConnectionService.isConnectedToInternet(MainApplication.applicationContext())).thenReturn(status)
        service.setInternetConnectionServicce(mockInternetConnectionService)
    }


    @Test
    fun requestResponseWhenNoValidDataAndInternetConnection() {
        setInternetConnection(true)

        initEmptyPreferences()

        Mockito.`when`(mockPoiGoogleRetriever.requestPoisAPI(Mockito.any(), Mockito.any(), Mockito.any())).then {
            val onSuccess = it.arguments[1] as ((List<PointOfInterest>) -> Unit)
            onSuccess(testPoiList)
        }

        service.setResponseFetcher(mockPoiGoogleRetriever)

        val customOnSuccess: (List<PointOfInterest>) -> Unit = {
            Assert.assertEquals(testPoiList, it)
        }

        service.getResponse(testPosition, customOnSuccess, { assert(false) })
    }

    @Test
    fun requestResponseWhenNoValidDataAndNoInternetConnectionCallsOnError() {
        setInternetConnection(false)

        initEmptyPreferences()
        service.getResponse(LatLng(0.0, 0.0), { assert(false) }, { assert(true) })
    }

    @Test
    fun requestResponseWhenObjectDataIsValid() {
        service.lastResponse = testPoiList
        service.responseTimestamp = System.currentTimeMillis() / 1000

        val customOnSuccess: (List<PointOfInterest>) -> Unit = {
            Assert.assertEquals(testPoiList, it)
        }

        service.getResponse(LatLng(0.0, 0.0), customOnSuccess, { assert(false) })
    }

    @Test
    fun requestResponseWhenCachedDataIsValid() {
        initPreferencesWithData(System.currentTimeMillis() / 1000)

        val customOnSuccess: (List<PointOfInterest>) -> Unit = {
            Assert.assertEquals(testPoiList, it)
        }

        service.getResponse(testPosition, customOnSuccess, { assert(false) })
    }

    @Test
    fun requestResponseWhenObjectDataIsOldButOnlyOption() {
        setInternetConnection(false)
        initEmptyPreferences()

        service.lastResponse = testPoiList
        service.responseTimestamp = 1L

        val customOnSuccess: (List<PointOfInterest>) -> Unit = {
            Assert.assertEquals(testPoiList, it)
        }

        service.getResponse(LatLng(0.0, 0.0), customOnSuccess, { assert(false) })
    }

    @Test
    fun requestResponseWhenCachedDataIsOldButOnlyOption() {
        setInternetConnection(false)
        initPreferencesWithData(1L)

        val customOnSuccess: (List<PointOfInterest>) -> Unit = {
            Assert.assertEquals(testPoiList, it)
        }

        service.getResponse(testPosition, customOnSuccess, { assert(false) })
    }
}