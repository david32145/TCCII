package br.ufc.crateus.tcc.components

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import br.ufc.crateus.tcc.BuildConfig
import br.ufc.crateus.tcc.services.MyAccessibilityService
import br.ufc.crateus.tcc.utils.AccessibilityUtils
import com.google.android.material.button.MaterialButton

class MyButton : MaterialButton {
    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(AccessibilityUtils.isInAccessibilityMode(context)) {
            return false
        }
        return super.onTouchEvent(event)
    }
}