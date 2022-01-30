package br.ufc.crateus.tcc.components

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageButton
import br.ufc.crateus.tcc.utils.AccessibilityUtils

class MyImageButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageButton(context, attrs) {
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(AccessibilityUtils.isInAccessibilityMode(context)) {
            return false
        }
        return super.onTouchEvent(event)
    }
}