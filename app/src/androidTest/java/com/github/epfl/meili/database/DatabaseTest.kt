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
        db.addObserver { obs, _ ->
            assertEquals(obs, db)
        }

        db.notifyObservers()
        db.addElement("uid", PointOfInterest())
        db.onDestroy()
    }

    class MockDatabase() : Database<PointOfInterest>("mockKey") {
        override var elements: Map<String, PointOfInterest> = HashMap()

        init {
            elements.plus(Pair("uid", PointOfInterest()))
        }

        override fun onDestroy() {

        }

        override fun addElement(uid: String, element: PointOfInterest?) {

        }

    }
}