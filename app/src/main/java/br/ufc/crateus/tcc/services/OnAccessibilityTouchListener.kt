package br.ufc.crateus.tcc.services

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import java.lang.Exception
import kotlin.math.abs

open class OnAccessibilityTouchListener(
    context: Context
) : View.OnTouchListener {
    private val gestureDetector = GestureDetector(context, GestureListener())

    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        if (p1?.action == MotionEvent.ACTION_POINTER_2_DOWN) {
            Log.d("OnAccessibilityTouchListener", "onTouchDown")
            onTouchDown()
        }
        if (p1?.action == MotionEvent.ACTION_POINTER_2_UP) {
            Log.d("OnAccessibilityTouchListener", "onTouchUp")
            onTouchUp()
        }
        return gestureDetector.onTouchEvent(p1)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent?): Boolean {
            return onDoubleTap()
        }


        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            return onSingleTapConfirmed()
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            var result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                        result = true
                    }
                } else if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom()
                    } else {
                        onSwipeTop()
                    }
                    result = true
                }
            } catch (err: Exception) {
                err.printStackTrace()
            }
            return result
        }
    }

    open fun onTouchDown() {}
    open fun onTouchUp() {}
    open fun onSwipeTop() {}
    open fun onSwipeRight() {}
    open fun onSwipeBottom() {}
    open fun onSwipeLeft() {}
    open fun onDoubleTap(): Boolean = false
    open fun onSingleTapConfirmed(): Boolean = false
}