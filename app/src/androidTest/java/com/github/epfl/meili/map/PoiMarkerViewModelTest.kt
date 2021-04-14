package com.github.epfl.meili.map

import android.location.Location
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.github.epfl.meili.database.Database
import com.github.epfl.meili.poi.PoiService
import com.github.epfl.meili.poi.PointOfInterest
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.*
import junit.framework.Assert.assertEquals
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class PoiMarkerViewModelTest {
    private val mockLocation: Location = Mockito.mock(Location::class.java)

    private var service: PoiMarkerViewModel = PoiMarkerViewModel()
    private val testPoiList = ArrayList<PointOfInterest>()
    private val poi1 = PointOfInterest(41.075000, 1.130870, "place1", "place1")
    private val poi2 = PointOfInterest(41.063563, 1.083658, "place2", "place2")
    private val testPosition = LatLng(41.075534, 1.131070)

    init {
        setupMocks()
        testPoiList.add(poi1)
        testPoiList.add(poi2)
    }

    private fun setupMocks() {
        Mockito.`when`(mockLocation.longitude).thenReturn(testPosition.longitude)
        Mockito.`when`(mockLocation.latitude).thenReturn(testPosition.latitude)
    }

    @Test
    fun poisReceivedFromServiceAddedProperlyBeforeLocation() {
        val mockPoiService = Mockito.mock(PoiService::class.java)
        val expectedPoiMap = HashMap<String, PointOfInterest>()
        val expectedStatusMap = HashMap<String, PoiMarkerViewModel.PointOfInterestStatus>()
        expectedPoiMap.put(poi1.uid, poi1)
        expectedPoiMap.put(poi2.uid, poi2)

        expectedStatusMap.put(poi1.uid, PoiMarkerViewModel.PointOfInterestStatus.VISITED)
        expectedStatusMap.put(poi2.uid, PoiMarkerViewModel.PointOfInterestStatus.VISIBLE)

        val databaseMap = HashMap<String, PointOfInterest>()
        databaseMap.put(poi1.uid, poi1)

        val mockDatabase = Mockito.mock(Database::class.java)
        Mockito.`when`(mockDatabase.values).thenReturn(databaseMap)

        Mockito.`when`(
                mockPoiService.requestPois(
                        Mockito.any(LatLng::class.java),
                        Mockito.any(),
                        Mockito.any()
                )
        )
                .then {
                    val onSuccess = it.arguments[1] as ((List<PointOfInterest>) -> Unit)
                    onSuccess(testPoiList)
                    return@then null
                }

        UiThreadStatement.runOnUiThread {
            service.setPoiService(mockPoiService)

            service.setDatabase(mockDatabase as Database<PointOfInterest>)

            service.onLocationChanged(mockLocation)

            assertEquals(expectedPoiMap, service.mPointsOfInterest.value)
            assertEquals(
                    expectedStatusMap,
                    service.mPointsOfInterestStatus.value
            )
        }
    }


    @Test
    fun setPoiVisitedAddsPoi() {
        val mockDatabase = Mockito.mock(Database::class.java)
        Mockito.`when`(mockDatabase.addElement(Mockito.any(), Mockito.any())).then {
            assertEquals(it.arguments[0], poi1.uid)
            assertEquals(it.arguments[1], poi1)

            return@then null
        }

        UiThreadStatement.runOnUiThread {
            val poiStatusMap = HashMap<String, PoiMarkerViewModel.PointOfInterestStatus>()
            poiStatusMap.put(poi1.uid, PoiMarkerViewModel.PointOfInterestStatus.REACHABLE)
            service.mPointsOfInterestStatus.value = poiStatusMap
            service.setDatabase(mockDatabase as Database<PointOfInterest>)
            service.setPoiVisited(poi1)
        }
    }


    @Test
    fun reachablePoi() {
        val mockPoiService = Mockito.mock(PoiService::class.java)
        val expectedPoiMap = HashMap<String, PointOfInterest>()
        val expectedStatusMap = HashMap<String, PoiMarkerViewModel.PointOfInterestStatus>()
        expectedPoiMap.put(poi1.uid, poi1)
        expectedPoiMap.put(poi2.uid, poi2)

        expectedStatusMap.put(poi1.uid, PoiMarkerViewModel.PointOfInterestStatus.REACHABLE)
        expectedStatusMap.put(poi2.uid, PoiMarkerViewModel.PointOfInterestStatus.VISIBLE)

        val databaseMap = HashMap<String, PointOfInterest>()
        databaseMap.put(poi1.uid, poi1)

        val mockDatabase = Mockito.mock(Database::class.java)
        Mockito.`when`(mockDatabase.values).thenReturn(databaseMap)

        Mockito.`when`(
                mockPoiService.requestPois(
                        Mockito.any(LatLng::class.java),
                        Mockito.any(),
                        Mockito.any()
                )
        )
                .then {
                    val onSuccess = it.arguments[1] as ((List<PointOfInterest>) -> Unit)
                    onSuccess(testPoiList)
                    return@then null
                }

        val reachablePoiList = ArrayList<PointOfInterest>()
        reachablePoiList.add(poi1)

        Mockito.`when`(
                mockPoiService.getReachablePoi(
                        Mockito.any(LatLng::class.java),
                        Mockito.any(),
                        Mockito.any()
                )
        )
                .thenReturn(reachablePoiList)

        UiThreadStatement.runOnUiThread {
            service.setPoiService(mockPoiService)

            service.setDatabase(mockDatabase as Database<PointOfInterest>)

            service.onLocationChanged(mockLocation)

            assertEquals(expectedPoiMap, service.mPointsOfInterest.value)
            assertEquals(
                    expectedStatusMap,
                    service.mPointsOfInterestStatus.value
            )
        }
    }

    @Test
    fun updateTest() {
        val mockPoiService = Mockito.mock(PoiService::class.java)
        val expectedPoiMap = HashMap<String, PointOfInterest>()
        val expectedStatusMap = HashMap<String, PoiMarkerViewModel.PointOfInterestStatus>()
        expectedPoiMap.put(poi1.uid, poi1)
        expectedPoiMap.put(poi2.uid, poi2)

        expectedStatusMap.put(poi1.uid, PoiMarkerViewModel.PointOfInterestStatus.VISITED)
        expectedStatusMap.put(poi2.uid, PoiMarkerViewModel.PointOfInterestStatus.VISITED)

        Mockito.`when`(mockPoiService.getReachablePoi(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(ArrayList())
        val databaseMap = HashMap<String, PointOfInterest>()
        databaseMap.put(poi1.uid, poi1)
        databaseMap.put(poi2.uid, poi2)

        val mockDatabase = Mockito.mock(Database::class.java)
        Mockito.`when`(mockDatabase.values).thenReturn(databaseMap)

        val reachablePoiList = ArrayList<PointOfInterest>()
        reachablePoiList.add(poi1)

        UiThreadStatement.runOnUiThread {
            service.setDatabase(mockDatabase as Database<PointOfInterest>)
            service.setPoiService(mockPoiService)
            service.onLocationChanged(mockLocation)
            service.update(null, null)

            assertEquals(expectedPoiMap, service.mPointsOfInterest.value)
            assertEquals(
                    expectedStatusMap,
                    service.mPointsOfInterestStatus.value
            )
        }
    }
}