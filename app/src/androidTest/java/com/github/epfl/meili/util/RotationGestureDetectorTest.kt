package com.github.epfl.meili.util


import android.os.SystemClock
import android.view.MotionEvent.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class RotationGestureDetectorTest {

    private val mockListener: RotationGestureDetector.OnRotationGestureListener =
        mock(RotationGestureDetector.OnRotationGestureListener::class.java)
    private lateinit var gestureDetector: RotationGestureDetector

    @Before
    fun setup() {
        gestureDetector = RotationGestureDetector(mockListener)
    }

    //TODO: no assertions here, is it possible to add some?
    @Test
    fun performActionDown() {
        val event = obtain(
            SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis(),
            ACTION_DOWN,
            0F,
            0F,
            0
        )
        gestureDetector.onTouchEvent(event)
    }

    @Test
    fun performActionPointerDown() {
        val setupEvent1 = obtain(
            SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis(),
            ACTION_DOWN,
            0F,
            0F,
            0
        )
        gestureDetector.onTouchEvent(setupEvent1)

        val event = obtain(
            SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis(),
            ACTION_POINTER_DOWN,
            0F,
            0F,
            0
        )
        gestureDetector.onTouchEvent(event)
    }

    @Test
    fun performActionMove() {
        val setupEvent1 = obtain(
            SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis(),
            ACTION_DOWN,
            0F,
            0F,
            0
        )
        gestureDetector.onTouchEvent(setupEvent1)

        val setupEvent2 = obtain(
            SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis(),
            ACTION_POINTER_DOWN,
            1F,
            1F,
            0
        )
        gestureDetector.onTouchEvent(setupEvent2)

        val event = obtain(
            SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis(),
            ACTION_MOVE,
            3F,
            3F,
            0
        )
        gestureDetector.onTouchEvent(event)
    }

    @Test
    fun performActionUp() {
        val event = obtain(
            SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis(),
            ACTION_UP,
            0F,
            0F,
            0
        )
        gestureDetector.onTouchEvent(event)
    }

    @Test
    fun performActionPointerUp() {
        val event = obtain(
            SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis(),
            ACTION_POINTER_UP,
            0F,
            0F,
            0
        )
        gestureDetector.onTouchEvent(event)
    }

    @Test
    fun performActionCancel() {
        val event = obtain(
            SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis(),
            ACTION_CANCEL,
            0F,
            0F,
            0
        )
        gestureDetector.onTouchEvent(event)
    }
}