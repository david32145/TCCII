package br.ufc.crateus.tcc.components

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import br.ufc.crateus.tcc.utils.AccessibilityUtils
import com.google.android.material.textfield.TextInputLayout

class MyTextInputLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputLayout(context, attrs) {

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(AccessibilityUtils.isInAccessibilityMode(context)) {
            return false
        }
        return super.onTouchEvent(event)
    }
}