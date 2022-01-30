package br.ufc.crateus.tcc.components

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import br.ufc.crateus.tcc.utils.AccessibilityUtils

class MyAppCompatImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(AccessibilityUtils.isInAccessibilityMode(context)) {
            return false
        }
        return super.onTouchEvent(event)
    }
}