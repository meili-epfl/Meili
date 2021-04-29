package com.github.epfl.meili.util

import android.view.MotionEvent
import android.widget.ImageView
import kotlin.math.atan2

// Reference used : https://stackoverflow.com/questions/10682019/android-two-finger-rotation
/** Detects two finger rotation motions */
class RotationGestureDetector(listener: OnRotationGestureListener, imageView: ImageView) {

    private val TAG = "RotationGestureDetector"

    private val imageView = imageView

    // Initial finger position values
    private var x1 = 0f
    private var y1 = 0f
    private var x2 = 0f
    private var y2 = 0f

    // Event pointer IDs for each finger
    private val INVALID_PTR_ID = -1
    private var ptrID1 = INVALID_PTR_ID
    private var ptrID2 = INVALID_PTR_ID

    private val listener = listener // Object which can be rotated using two fingers

    private var angle = 0f // rotation angle detected

    /** Objects that need rotation functionality need to implement this interface */
    interface OnRotationGestureListener {
        fun onRotation(angle: Float)
    }

    /** Computes correct indices and positions based on the touch event */
    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> { // First touch
                ptrID1 = event.getPointerId(event.actionIndex) // first finger down's pointer
            }
            MotionEvent.ACTION_POINTER_DOWN -> { // Second touch
                handleActionPointerDown(event)
            }
            MotionEvent.ACTION_MOVE -> { // Either finger moves
                handleActionMove(event)
            }
            MotionEvent.ACTION_UP -> { // First finger lifted
                ptrID1 = INVALID_PTR_ID
            }
            MotionEvent.ACTION_POINTER_UP -> { // Second finger lifted
                ptrID2 = INVALID_PTR_ID
            }
            MotionEvent.ACTION_CANCEL -> { // Action is canceled
                ptrID1 = INVALID_PTR_ID
                ptrID2 = INVALID_PTR_ID
            }
        }

        return true
    }

    private fun handleActionPointerDown(event: MotionEvent) {
        ptrID2 =
            event.getPointerId(event.actionIndex) // second finger down's pointer ID

        x1 = event.getX(event.findPointerIndex(ptrID1))
        y1 = event.getY(event.findPointerIndex(ptrID1))
        x2 = event.getX(event.findPointerIndex(ptrID2))
        y2 = event.getY(event.findPointerIndex(ptrID2))
    }

    private fun handleActionMove(event: MotionEvent) {
        if (ptrID1 != INVALID_PTR_ID && ptrID2 != INVALID_PTR_ID) {
            // Get new positions
            val newx1 = event.getX(event.findPointerIndex(ptrID1))
            val newy1 = event.getY(event.findPointerIndex(ptrID1))
            val newx2 = event.getX(event.findPointerIndex(ptrID2))
            val newy2 = event.getY(event.findPointerIndex(ptrID2))

            // compute angle and notify listener
            angle = angleBetweenLines(newx1, newy1, newx2, newy2)
            listener.onRotation(angle)
        }
    }

    /** Compute angle between lines formed by previous positions and new positions */
    private fun angleBetweenLines(newx1: Float, newy1: Float, newx2: Float, newy2: Float): Float {
        // Compute angles of both lines
        val angle1 = atan2(y1 - y2, x1 - x2)
        val angle2 = atan2(newy1 - newy2, newx1 - newx2)

        // Transform to degrees between -180 and 180
        var newAngle = Math.toDegrees((angle2 - angle1).toDouble()).toFloat() % 360
        if (newAngle < -180f) newAngle += 360.0f
        if (newAngle > 180f) newAngle -= 360.0f

        return newAngle
    }

}