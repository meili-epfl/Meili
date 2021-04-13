package com.github.epfl.meili.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.epfl.meili.poi.PointOfInterest
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    @Test
    fun addObserverTest() {
        val db: Database<PointOfInterest> = MockDatabase()
        db.addObserver({ obs, arg ->
            assertEquals(obs, db)
        })

        db.notifyObservers()
        db.addElement("uid", PointOfInterest())
        db.onDestroy()
    }

    class MockDatabase() : Database<PointOfInterest>("mockPath") {
        override var values: Map<String, PointOfInterest> = HashMap()

        init {
            values.plus(Pair("uid", PointOfInterest()))
        }

        override fun addElement(uid: String?, element: PointOfInterest?) {

        }

        override fun onDestroy() {

        }
    }
}